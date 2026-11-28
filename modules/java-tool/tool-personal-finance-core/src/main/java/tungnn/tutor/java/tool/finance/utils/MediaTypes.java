package tungnn.tutor.java.tool.finance.utils;

public final class MediaTypes {

    public static final String CSV = "text/csv";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PDF = "application/pdf";
    public static final String EML = "message/rfc822";
    public static final String TEXT = "text/plain";

    private MediaTypes() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static boolean isTabular(String mediaType) {
        return CSV.equals(mediaType) || XLSX.equals(mediaType);
    }
}
