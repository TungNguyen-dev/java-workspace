package tungnn.tutor.java.tool.finance.core.sink;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.Money;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.core.model.Transaction;

class JsonlFinanceSinkTest {

    @TempDir
    Path dir;

    @Test
    void shouldAppendOneLinePerTransactionAndReportEachOutcome() throws Exception {
        var sink = new JsonlFinanceSink("staging", dir);
        var outcomes = new ArrayList<WriteOutcome>();

        sink.writeTransactions(List.of(tx("t1", 500_000), tx("t2", 120_000)), outcomes::add);

        assertThat(outcomes).extracting(WriteOutcome::status).containsOnly(WriteOutcome.Status.WRITTEN);
        assertThat(Files.readAllLines(dir.resolve("transactions.jsonl"))).hasSize(2);
    }

    private static Transaction tx(String id, long amount) {
        return new Transaction(
                id,
                "acc-1",
                LocalDate.of(2026, 11, 1),
                Money.vnd(amount),
                Direction.DEBIT,
                "SHOPEE",
                null,
                null,
                SourceKind.EMAIL,
                "mail");
    }
}
