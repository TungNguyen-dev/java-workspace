package tungnn.tutor.java.tool.meddoc.core.chat;

@FunctionalInterface
public interface MarkdownChat {
    String chat(String system, String user);
}
