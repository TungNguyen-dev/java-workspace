package tungnn.tutor.java.tool.finance.core.extract;

import tungnn.tutor.java.tool.finance.core.model.RawDocument;

public interface TransactionExtractor {

    boolean supports(RawDocument document);

    ExtractionResult extract(RawDocument document);
}
