package tungnn.tutor.java.tool.finance.core.extract;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.validate.AmountGuard;
import tungnn.tutor.java.tool.finance.core.validate.BalanceReconciler;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;
import tungnn.tutor.java.tool.finance.utils.PromptUtils;

public final class LlmTransactionExtractor implements TransactionExtractor {

    private final LlmClient client;
    private final Map<String, String> accountBySource;
    private final String systemPrompt = PromptUtils.load("extract-transactions.md");

    public LlmTransactionExtractor(LlmClient client, Map<String, String> accountBySource) {
        this.client = client;
        this.accountBySource = Map.copyOf(accountBySource);
    }

    @Override
    public boolean supports(RawDocument document) {
        return document.text() != null
                && !document.text().isBlank()
                && !MediaTypes.isTabular(document.mediaType())
                && accountBySource.containsKey(document.sourceId());
    }

    @Override
    public ExtractionResult extract(RawDocument document) {
        var batch = client.chat(systemPrompt, document.text(), LlmExtractedBatch.class);
        if (batch == null || batch.transactions() == null) {
            return ExtractionResult.of(List.of());
        }
        var guard = new AmountGuard(document.text());
        var accountId = accountBySource.get(document.sourceId());
        var transactions = new ArrayList<Transaction>();
        var issues = new ArrayList<String>();
        var index = 0;
        for (var item : batch.transactions()) {
            var position = index++;
            if (item.amount() == null || item.amount().signum() <= 0 || !guard.isPresent(item.amount())) {
                issues.add("Rejected item %d of %s: amount %s is not in the source text"
                        .formatted(position, document.origin(), item.amount()));
                continue;
            }
            try {
                var date = LocalDate.parse(item.date());
                var direction = Direction.valueOf(item.direction().strip().toUpperCase());
                transactions.add(new Transaction(
                        UUID.nameUUIDFromBytes((document.sourceId() + "|" + document.origin() + "|" + position
                                        + "|" + date + "|" + item.amount().toPlainString())
                                        .getBytes())
                                .toString(),
                        accountId,
                        date,
                        new Money(item.amount(), Money.VND),
                        direction,
                        item.description(),
                        item.reference(),
                        null,
                        document.kind(),
                        document.sourceId()));
            } catch (DateTimeParseException | IllegalArgumentException | NullPointerException e) {
                issues.add("Rejected item %d of %s: invalid date or direction".formatted(position, document.origin()));
            }
        }
        var mismatch = BalanceReconciler.check(batch.openingBalance(), batch.closingBalance(), transactions);
        if (mismatch.isPresent()) {
            issues.add(document.origin() + ": " + mismatch.get() + "; nothing from this document was accepted");
            return new ExtractionResult(List.of(), issues);
        }
        return new ExtractionResult(transactions, issues);
    }
}
