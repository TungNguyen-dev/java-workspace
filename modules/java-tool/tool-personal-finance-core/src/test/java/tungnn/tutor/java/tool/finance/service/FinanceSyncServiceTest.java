package tungnn.tutor.java.tool.finance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.finance.config.CategoryRule;
import tungnn.tutor.java.tool.finance.config.StatementLayout;
import tungnn.tutor.java.tool.finance.core.categorize.RuleCategorizer;
import tungnn.tutor.java.tool.finance.core.extract.TabularStatementExtractor;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.sink.FinanceSink;
import tungnn.tutor.java.tool.finance.core.sink.JsonlFinanceSink;
import tungnn.tutor.java.tool.finance.core.sink.WriteOutcome;
import tungnn.tutor.java.tool.finance.core.source.FinanceSource;
import tungnn.tutor.java.tool.finance.core.source.SyncCursor;
import tungnn.tutor.java.tool.finance.core.state.FileSyncStateStore;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

class FinanceSyncServiceTest {

    @TempDir
    Path dir;

    private FinanceSource source;

    @BeforeEach
    void setUp() throws Exception {
        var csv = dir.resolve("stmt.csv");
        Files.writeString(csv, "Date,Desc,Amount\n01/11/2026,SHOPEE,-500.000\n02/11/2026,SALARY,15.000.000\n03/11/2026,GRAB,-80.000\n");
        source = new FinanceSource() {
            @Override
            public String id() {
                return "bank";
            }

            @Override
            public Stream<RawDocument> fetch(SyncCursor cursor) {
                return Stream.of(new RawDocument(
                        "bank", SourceKind.BANK_STATEMENT_FILE, csv.toString(), MediaTypes.CSV, null, null, null));
            }
        };
    }

    @Test
    void secondRunShouldWriteNothing() {
        var sink = new JsonlFinanceSink("staging", dir.resolve("out"));
        var service = service(sink);

        var first = service.sync(Set.of(), false);
        var second = service.sync(Set.of(), false);

        assertThat(first.written()).isEqualTo(3);
        assertThat(second.written()).isZero();
        assertThat(second.alreadyWritten()).isEqualTo(3);
    }

    @Test
    void dryRunShouldNotTouchTheSinkOrTheLedger() {
        var sink = new JsonlFinanceSink("staging", dir.resolve("out"));

        var report = service(sink).sync(Set.of(), true);

        assertThat(report.toWrite()).isEqualTo(3);
        assertThat(report.written()).isZero();
        assertThat(dir.resolve("out/transactions.jsonl")).doesNotExist();
    }

    @Test
    void rerunAfterSinkFailureShouldOnlyWriteTheMissingRows() {
        var flaky = new FlakySink(2);
        var firstReport = service(flaky).sync(Set.of(), false);
        assertThat(firstReport.written()).isEqualTo(2);
        assertThat(firstReport.failed()).isEqualTo(1);

        flaky.failAt = -1;
        var secondReport = service(flaky).sync(Set.of(), false);

        assertThat(secondReport.written()).isEqualTo(1);
        assertThat(flaky.stored).hasSize(3).doesNotHaveDuplicates();
    }

    @Test
    void shouldCategorizeByRule() {
        var sink = new JsonlFinanceSink("staging", dir.resolve("out"));
        service(sink).sync(Set.of(), false);

        assertThat(dir.resolve("out/transactions.jsonl")).content().contains("\"category\":\"Shopping\"");
    }

    private FinanceSyncService service(FinanceSink sink) {
        var layout = new StatementLayout("l", "csv", 1, 0, "dd/MM/yyyy", 1, null, 2, null, null);
        var extractor = new TabularStatementExtractor(Map.of("bank", new TabularStatementExtractor.Binding(layout, "acc-1")));
        return new FinanceSyncService(
                List.of(source),
                List.of(extractor),
                new RuleCategorizer(List.of(new CategoryRule("shopee", "Shopping"))),
                sink,
                new FileSyncStateStore(dir.resolve("state")),
                List.of(new Account("acc-1", "Main", SourceKind.BANK_STATEMENT_FILE, "VND")));
    }

    private static final class FlakySink implements FinanceSink {
        private int failAt;
        private final List<String> stored = new java.util.ArrayList<>();

        private FlakySink(int failAt) {
            this.failAt = failAt;
        }

        @Override
        public String id() {
            return "flaky";
        }

        @Override
        public void upsertAccounts(List<Account> accounts) {}

        @Override
        public void writeTransactions(List<Transaction> transactions, Consumer<WriteOutcome> onEach) {
            for (var i = 0; i < transactions.size(); i++) {
                if (i + 1 == failAt) {
                    onEach.accept(WriteOutcome.failed(transactions.get(i), "browser crashed"));
                    continue;
                }
                stored.add(transactions.get(i).id());
                onEach.accept(WriteOutcome.written(transactions.get(i)));
            }
        }
    }
}
