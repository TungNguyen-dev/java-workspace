package tungnn.tutor.java.tool.finance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.mail.AppleMailUtils;
import tungnn.tutor.java.tool.finance.config.FinanceConfig;
import tungnn.tutor.java.tool.finance.core.categorize.Categorizer;
import tungnn.tutor.java.tool.finance.core.categorize.ChainCategorizer;
import tungnn.tutor.java.tool.finance.core.categorize.LlmCategorizer;
import tungnn.tutor.java.tool.finance.core.categorize.RuleCategorizer;
import tungnn.tutor.java.tool.finance.core.extract.LlmTransactionExtractor;
import tungnn.tutor.java.tool.finance.core.extract.TabularStatementExtractor;
import tungnn.tutor.java.tool.finance.core.extract.TransactionExtractor;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.core.sink.JsonlFinanceSink;
import tungnn.tutor.java.tool.finance.core.source.AppleMailSource;
import tungnn.tutor.java.tool.finance.core.source.FileDropSource;
import tungnn.tutor.java.tool.finance.core.source.FinanceSource;
import tungnn.tutor.java.tool.finance.core.source.ImapMailSource;
import tungnn.tutor.java.tool.finance.core.state.FileSyncStateStore;
import tungnn.tutor.java.tool.finance.service.DailyNoteService;
import tungnn.tutor.java.tool.finance.service.FinanceSyncService;

public final class FinanceSyncAssembler {

    private FinanceSyncAssembler() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public record FinanceApp(FinanceSyncService sync, DailyNoteService notes) {}

    public static FinanceApp assemble(FinanceConfig config, Map<String, String> env) {
        var storage = Path.of(firstNonBlank(env.get("FINANCE_STORAGE_DIR"), config.storageDir(), "storage/finance"));
        var layouts = new HashMap<String, tungnn.tutor.java.tool.finance.config.StatementLayout>();
        config.layouts().forEach(layout -> layouts.put(layout.id(), layout));

        var sources = new ArrayList<FinanceSource>();
        var accounts = new ArrayList<Account>();
        var tabular = new HashMap<String, TabularStatementExtractor.Binding>();
        var llmAccounts = new HashMap<String, String>();

        for (var file : config.fileSources()) {
            var directory = firstNonBlank(env.get("FINANCE_INBOX_DIR"), null, null);
            var path = directory == null
                    ? Path.of(file.directory())
                    : Path.of(directory).resolve(file.directory());
            sources.add(new FileDropSource(file.id(), file.kind(), path));
            accounts.add(new Account(file.accountId(), file.accountName(), file.kind(), "VND"));
            if (file.layoutId() != null) {
                var layout = layouts.get(file.layoutId());
                if (layout == null) {
                    throw new IllegalStateException("Unknown layout " + file.layoutId() + " in source " + file.id());
                }
                tabular.put(file.id(), new TabularStatementExtractor.Binding(layout, file.accountId()));
            }
            llmAccounts.put(file.id(), file.accountId());
        }

        for (var mail : config.mailSources()) {
            var host = require(env, "FINANCE_IMAP_HOST");
            var user = require(env, "FINANCE_IMAP_USER");
            var password = require(env, "FINANCE_IMAP_PASSWORD");
            sources.add(ImapMailSource.imap(mail.id(), host, user, password, mail.folder(), mail.senders()));
            accounts.add(new Account(mail.accountId(), mail.accountName(), SourceKind.EMAIL, "VND"));
            llmAccounts.put(mail.id(), mail.accountId());
        }
        for (var mail : config.appleMailSources()) {
            var root = AppleMailUtils.resolveMailRoot(
                    firstNonBlank(env.get("FINANCE_APPLE_MAIL_ROOT"), mail.mailRoot(), null));
            sources.add(new AppleMailSource(mail.id(), root, mail.mailboxes(), mail.senders()));
            accounts.add(new Account(mail.accountId(), mail.accountName(), SourceKind.EMAIL, "VND"));
            llmAccounts.put(mail.id(), mail.accountId());
        }
        // TODO: register BankWebSource instances once a BankWebScraper exists for a concrete bank.

        var providerName = env.get("FINANCE_LLM_PROVIDER");
        var llm = providerName == null || providerName.isBlank()
                ? null
                : LlmClientUtils.fromEnv(
                        LlmProvider.valueOf(providerName.strip().toUpperCase()));

        var extractors = new ArrayList<TransactionExtractor>();
        extractors.add(new TabularStatementExtractor(tabular));
        var categorizers = new ArrayList<Categorizer>();
        categorizers.add(new RuleCategorizer(config.categoryRules()));
        if (llm != null) {
            extractors.add(new LlmTransactionExtractor(llm, llmAccounts));
            categorizers.add(new LlmCategorizer(llm, config.categories()));
        }

        // TODO: replace the staging sink with SeleniumFinanceSink once the target web app is available.
        var sinkId = config.sinkId() == null ? "staging" : config.sinkId();
        var sink = new JsonlFinanceSink(sinkId, storage.resolve("out"));
        var state = new FileSyncStateStore(storage.resolve("state"));
        var sync =
                new FinanceSyncService(sources, extractors, new ChainCategorizer(categorizers), sink, state, accounts);
        var notes = new DailyNoteService(state, sinkId, storage.resolve("out").resolve("daily"), accounts);
        return new FinanceApp(sync, notes);
    }

    private static String require(Map<String, String> env, String name) {
        var value = env.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing environment variable " + name);
        }
        return value;
    }

    private static String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return fallback;
    }
}
