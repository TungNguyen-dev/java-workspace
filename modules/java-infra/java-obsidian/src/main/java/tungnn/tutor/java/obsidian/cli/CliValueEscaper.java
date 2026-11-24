package tungnn.tutor.java.obsidian.cli;

public final class CliValueEscaper {

    private CliValueEscaper() {}

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        var sb = new StringBuilder(value.length() + 16);
        for (var i = 0; i < value.length(); i++) {
            var c = value.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\t' -> sb.append("\\t");
                case '\r' -> {}
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
