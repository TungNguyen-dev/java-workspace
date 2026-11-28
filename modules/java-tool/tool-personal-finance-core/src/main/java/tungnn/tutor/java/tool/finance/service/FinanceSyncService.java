package tungnn.tutor.java.tool.finance.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import tungnn.tutor.java.tool.finance.core.categorize.Categorizer;
import tungnn.tutor.java.tool.finance.core.dedupe.Deduplicator;
import tungnn.tutor.java.tool.finance.core.extract.TransactionExtractor;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.normalize.TransactionNormalizer;
import tungnn.tutor.java.tool.finance.core.sink.FinanceSink;
import tungnn.tutor.java.tool.finance.core.sink.WriteOutcome;
import tungnn.tutor.java.tool.finance.core.source.FinanceSource;
import tungnn.tutor.java.tool.finance.core.source.SyncCursor;
import tungnn.tutor.java.tool.finance.core.state.SyncStateStore;

public final class FinanceSyncService {

    private final List<FinanceSource> sources;
    private final List<TransactionExtractor> extractors;
    private final Categorizer categorizer;
    private final FinanceSink sink;
    private final SyncStateStore state;
    private final List<Account> accounts;
    private final TransactionNormalizer normalizer = new TransactionNormalizer();
    private final Deduplicator deduplicator = new Deduplicator();

    public FinanceSyncService(
            List<FinanceSource> sources,
            List<TransactionExtractor> extractors,
            Categorizer categorizer,
            FinanceSink sink,
            SyncStateStore state,
            List<Account> accounts) {
        this.sources = List.copyOf(sources);
        this.extractors = List.copyOf(extractors);
        this.categorizer = categorizer;
        this.sink = sink;
        this.state = state;
        this.accounts = List.copyOf(accounts);
    }

    public SyncReport sync(Set<String> onlySources, boolean dryRun) {
        var report = new SyncReport();
        var startedAt = Instant.now();
        var failedSources = new HashSet<String>();
        var extracted = new ArrayList<Transaction>();
        var active = sources.stream()
                .filter(s -> onlySources.isEmpty() || onlySources.contains(s.id()))
                .toList();

        for (var source : active) {
            collect(source, extracted, report, failedSources);
        }
        report.extracted(extracted.size());

        var ledger = state.ledger(sink.id());
        var writtenIds = ledger.stream().map(Transaction::id).collect(Collectors.toSet());
        var normalized = extracted.stream().map(normalizer::normalize).toList();
        var notYetWritten =
                normalized.stream().filter(tx -> !writtenIds.contains(tx.id())).toList();
        report.alreadyWritten(normalized.size() - notYetWritten.size());

        var deduped = deduplicator.deduplicate(notYetWritten, ledger);
        report.duplicates(deduped.duplicates());
        var toWrite = deduped.kept().stream().map(this::categorize).toList();
        report.toWrite(toWrite.size());

        if (!dryRun && !toWrite.isEmpty()) {
            sink.upsertAccounts(accounts);
            sink.writeTransactions(toWrite, outcome -> record(outcome, report));
        }
        // The cursor only moves when everything went through; the ledger keeps retries from duplicating rows.
        if (!dryRun && report.failed() == 0) {
            active.stream()
                    .filter(s -> !failedSources.contains(s.id()))
                    .forEach(s -> state.saveCursor(s.id(), new SyncCursor(startedAt)));
        }
        return report;
    }

    private void collect(FinanceSource source, List<Transaction> out, SyncReport report, Set<String> failedSources) {
        try (var documents = source.fetch(state.cursor(source.id()))) {
            documents.forEach(document -> {
                report.documentRead();
                if (!extract(document, out, report)) {
                    failedSources.add(source.id());
                }
            });
        } catch (RuntimeException e) {
            failedSources.add(source.id());
            report.issue("Source " + source.id() + " failed: " + e.getMessage());
        }
    }

    private boolean extract(RawDocument document, List<Transaction> out, SyncReport report) {
        var extractor = extractors.stream().filter(e -> e.supports(document)).findFirst();
        if (extractor.isEmpty()) {
            report.issue("No extractor for " + document.origin() + " (" + document.mediaType() + ")");
            return false;
        }
        try {
            var result = extractor.get().extract(document);
            out.addAll(result.transactions());
            result.issues().forEach(report::issue);
            return true;
        } catch (RuntimeException e) {
            report.issue("Extraction failed for " + document.origin() + ": " + e.getMessage());
            return false;
        }
    }

    private Transaction categorize(Transaction tx) {
        if (tx.category() != null) {
            return tx;
        }
        return categorizer.categorize(tx).map(tx::withCategory).orElse(tx);
    }

    private void record(WriteOutcome outcome, SyncReport report) {
        if (outcome.status() == WriteOutcome.Status.WRITTEN) {
            state.recordWritten(sink.id(), outcome.transaction());
            report.recordWritten(outcome.transaction().bookedOn());
        } else {
            report.recordFailed(outcome.reason());
        }
    }
}
