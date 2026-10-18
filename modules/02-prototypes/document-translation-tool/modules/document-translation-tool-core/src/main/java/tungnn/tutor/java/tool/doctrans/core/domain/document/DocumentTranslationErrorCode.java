package tungnn.tutor.java.tool.doctrans.core.domain.document;

public enum DocumentTranslationErrorCode {
  IO_ERROR("DOC_IO_ERROR", "Failed to read or write document file"),
  UNSUPPORTED_FORMAT("DOC_UNSUPPORTED_FORMAT", "Unsupported document format"),
  TRANSLATION_FAILED("DOC_TRANSLATION_FAILED", "Translation service call failed"),
  UNKNOWN_ERROR("DOC_UNKNOWN_ERROR", "An unknown error occurred");

  private final String code;
  private final String defaultMessage;

  DocumentTranslationErrorCode(String code, String defaultMessage) {
    this.code = code;
    this.defaultMessage = defaultMessage;
  }

  public String code() {
    return code;
  }

  public String defaultMessage() {
    return defaultMessage;
  }
}
