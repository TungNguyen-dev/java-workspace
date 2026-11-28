package tungnn.tutor.java.tool.finance.core.extract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;
import tungnn.tutor.java.tool.finance.core.model.SourceKind;
import tungnn.tutor.java.tool.finance.utils.MediaTypes;

class LlmTransactionExtractorTest {

    private static final String TEXT = "TK 1234 -500.000 VND tai SHOPEE. So du 9.500.000 VND";

    private final RawDocument document =
            new RawDocument("mail", SourceKind.EMAIL, "msg-1", MediaTypes.EML, TEXT, null, null);

    @Test
    void shouldRejectAmountsMissingFromTheSourceText() {
        var batch = new LlmExtractedBatch(
                List.of(
                        new LlmExtractedBatch.Item("2026-11-02", new BigDecimal("500000"), "DEBIT", "SHOPEE", null),
                        new LlmExtractedBatch.Item("2026-11-02", new BigDecimal("700000"), "DEBIT", "MADE UP", null)),
                null,
                null);

        var result = extractorReturning(batch).extract(document);

        assertThat(result.transactions()).hasSize(1);
        assertThat(result.transactions().get(0).money().amount()).isEqualByComparingTo("500000");
        assertThat(result.issues()).anyMatch(issue -> issue.contains("700000"));
    }

    @Test
    void shouldDropTheWholeDocumentWhenBalancesDoNotReconcile() {
        var batch = new LlmExtractedBatch(
                List.of(new LlmExtractedBatch.Item("2026-11-02", new BigDecimal("500000"), "DEBIT", "SHOPEE", null)),
                new BigDecimal("10000000"),
                new BigDecimal("9000000"));

        var result = extractorReturning(batch).extract(document);

        assertThat(result.transactions()).isEmpty();
        assertThat(result.issues()).anyMatch(issue -> issue.contains("Balance mismatch"));
    }

    @Test
    void shouldAcceptWhenBalancesReconcile() {
        var batch = new LlmExtractedBatch(
                List.of(new LlmExtractedBatch.Item("2026-11-02", new BigDecimal("500000"), "DEBIT", "SHOPEE", null)),
                new BigDecimal("10000000"),
                new BigDecimal("9500000"));

        assertThat(extractorReturning(batch).extract(document).transactions()).hasSize(1);
    }

    private static LlmTransactionExtractor extractorReturning(LlmExtractedBatch batch) {
        var client = mock(LlmClient.class);
        when(client.chat(anyString(), anyString(), eq(LlmExtractedBatch.class))).thenReturn(batch);
        return new LlmTransactionExtractor(client, Map.of("mail", "acc-1"));
    }
}
