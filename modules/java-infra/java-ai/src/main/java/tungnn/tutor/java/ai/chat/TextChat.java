package tungnn.tutor.java.ai.chat;

@FunctionalInterface
public interface TextChat {
    String chat(String system, String user);
}
