package tungnn.tutor.java.tool.meddoc.core.llm;

@FunctionalInterface
public interface MarkdownChat {
    String chat(String system, String user);
}
