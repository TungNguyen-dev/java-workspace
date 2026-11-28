package tungnn.tutor.java.tool.meddoc.llm;

public interface MeteredChat {
    ChatReply chatWithUsage(String system, String user);
}
