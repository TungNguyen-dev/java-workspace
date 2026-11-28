package tungnn.tutor.java.tool.finance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.finance.TestTransactions;
import tungnn.tutor.java.tool.finance.core.model.Account;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.core.model.Transaction;
import tungnn.tutor.java.tool.finance.core.state.FileSyncStateStore;

class DailyNoteServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 28);

    @TempDir
    Path dir;

    private DailyNoteService service(FileSyncStateStore state) {
        return new DailyNoteService(
                state,
                "staging",
                dir.resolve("daily"),
                List.of(new Account("acc-1", "Vietcombank", SourceKind.EMAIL, "VND")));
    }

    @Test
    void shouldSummarizeTheDayAndOverwriteOnRerun() throws Exception {
        var state = new FileSyncStateStore(dir.resolve("state"));
        state.recordWritten(
                "staging",
                TestTransactions.debit("t1", SourceKind.EMAIL, "m", DAY, 500_000, "r1")
                        .withCategory("Shopping"));
        state.recordWritten("staging", TestTransactions.debit("t2", SourceKind.EMAIL, "m", DAY, 80_000, "r2"));
        state.recordWritten(
                "staging",
                new Transaction(
                        "t3",
                        "acc-1",
                        DAY,
                        Money.vnd(15_000_000),
                        Direction.CREDIT,
                        "Salary",
                        null,
                        "Income",
                        SourceKind.EMAIL,
                        "m"));
        state.recordWritten("staging", TestTransactions.debit("t4", SourceKind.EMAIL, "m", DAY.minusDays(1), 1, "r4"));

        var first = service(state).write(DAY);
        var content = Files.readString(first);
        service(state).write(DAY);

        assertThat(first.getFileName().toString()).isEqualTo("2026-11-28.md");
        assertThat(content)
                .contains("- Income: 15.000.000 ₫")
                .contains("- Expense: 580.000 ₫")
                .contains("- Net: 14.420.000 ₫")
                .contains("| Vietcombank | Expense | 500.000 ₫ | desc t1 | Shopping |")
                .contains("- Shopping: 500.000 ₫")
                .contains("- Uncategorized: 80.000 ₫")
                .doesNotContain("desc t4");
        assertThat(Files.readString(first)).isEqualTo(content);
    }

    @Test
    void shouldWriteAnEmptyNoteWhenNothingHappened() throws Exception {
        var state = new FileSyncStateStore(dir.resolve("state"));

        var file = service(state).write(DAY);

        assertThat(Files.readString(file)).contains("No transactions recorded.");
    }
}
