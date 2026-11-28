package tungnn.tutor.java.ai.chat;

public interface MeteredChat {
    ChatReply chatWithUsage(String system, String user);
}
