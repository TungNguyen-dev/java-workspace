package tungnn.tutor.java.tool.finance.core.extract;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.finance.config.StatementLayout;
import tungnn.tutor.java.tool.finance.core.model.Direction;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

class TabularStatementExtractorTest {

    @TempDir
    Path dir;

    @Test
    void shouldReadSignedAmountColumnAndSkipFooterRows() throws Exception {
        var file = dir.resolve("stmt.csv");
        Files.writeString(
                file,
                "Date,Desc,Amount\n01/11/2026,SHOPEE,-500.000\n02/11/2026,SALARY,15.000.000\nTotal,,\n");
        var layout = new StatementLayout("l", "csv", 1, 0, "dd/MM/yyyy", 1, null, 2, null, null);
        var extractor = new TabularStatementExtractor(Map.of("bank", new TabularStatementExtractor.Binding(layout, "acc-1")));
        var document = new RawDocument(
                "bank", SourceKind.BANK_STATEMENT_FILE, file.toString(), MediaTypes.CSV, null, null, null);

        assertThat(extractor.supports(document)).isTrue();
        var result = extractor.extract(document);

        assertThat(result.transactions()).hasSize(2);
        var first = result.transactions().get(0);
        assertThat(first.direction()).isEqualTo(Direction.DEBIT);
        assertThat(first.money().amount()).isEqualByComparingTo("500000");
        assertThat(first.bookedOn()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(result.transactions().get(1).direction()).isEqualTo(Direction.CREDIT);
    }

    @Test
    void shouldReadSeparateDebitAndCreditColumns() throws Exception {
        var file = dir.resolve("stmt2.csv");
        Files.writeString(file, "01/11/2026,GROCERY,120.000,\n02/11/2026,REFUND,,50.000\n");
        var layout = new StatementLayout("l", "csv", 0, 0, "dd/MM/yyyy", 1, null, null, 2, 3);
        var extractor = new TabularStatementExtractor(Map.of("bank", new TabularStatementExtractor.Binding(layout, "acc-1")));
        var document = new RawDocument(
                "bank", SourceKind.BANK_STATEMENT_FILE, file.toString(), MediaTypes.CSV, null, null, null);

        var result = extractor.extract(document);

        assertThat(result.transactions()).extracting(t -> t.direction().name()).containsExactly("DEBIT", "CREDIT");
    }
}
