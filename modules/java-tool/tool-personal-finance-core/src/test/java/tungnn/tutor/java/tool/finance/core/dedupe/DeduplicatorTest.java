package tungnn.tutor.java.tool.finance.core.dedupe;

import static org.assertj.core.api.Assertions.assertThat;
import static tungnn.tutor.java.tool.finance.TestTransactions.debit;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;

class DeduplicatorTest {

    private final Deduplicator deduplicator = new Deduplicator();

    @Test
    void shouldKeepTheStatementRowWhenEmailMatchesWithinOneDay() {
        var email = debit("e1", SourceKind.EMAIL, "mail", LocalDate.of(2026, 11, 2), 500_000, null);
        var statement = debit("s1", SourceKind.BANK_STATEMENT_FILE, "stmt", LocalDate.of(2026, 11, 3), 500_000, "FT123");

        var result = deduplicator.deduplicate(List.of(email, statement), List.of());

        assertThat(result.kept()).hasSize(1);
        assertThat(result.kept().get(0).id()).isEqualTo("s1");
        assertThat(result.kept().get(0).reference()).isEqualTo("FT123");
        assertThat(result.duplicates()).isEqualTo(1);
    }

    @Test
    void shouldNotCollapseRepeatedPurchasesWithinOneSource() {
        var first = debit("a", SourceKind.BANK_STATEMENT_FILE, "stmt", LocalDate.of(2026, 11, 2), 30_000, null);
        var second = debit("b", SourceKind.BANK_STATEMENT_FILE, "stmt", LocalDate.of(2026, 11, 2), 30_000, null);

        assertThat(deduplicator.deduplicate(List.of(first, second), List.of()).kept()).hasSize(2);
    }

    @Test
    void shouldSkipIncomingRowAlreadyWrittenFromAnotherSource() {
        var written = debit("e1", SourceKind.EMAIL, "mail", LocalDate.of(2026, 11, 2), 500_000, null);
        var statement = debit("s1", SourceKind.BANK_STATEMENT_FILE, "stmt", LocalDate.of(2026, 11, 2), 500_000, null);

        var result = deduplicator.deduplicate(List.of(statement), List.of(written));

        assertThat(result.kept()).isEmpty();
        assertThat(result.duplicates()).isEqualTo(1);
    }

    @Test
    void shouldNotMatchDifferentReferences() {
        var a = debit("a", SourceKind.EMAIL, "mail", LocalDate.of(2026, 11, 2), 500_000, "R1");
        var b = debit("b", SourceKind.BANK_STATEMENT_FILE, "stmt", LocalDate.of(2026, 11, 2), 500_000, "R2");

        assertThat(deduplicator.deduplicate(List.of(a, b), List.of()).kept()).hasSize(2);
    }
}
