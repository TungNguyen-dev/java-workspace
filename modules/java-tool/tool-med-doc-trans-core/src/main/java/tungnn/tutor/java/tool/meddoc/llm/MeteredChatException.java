package tungnn.tutor.java.tool.meddoc.llm;

public final class MeteredChatException extends RuntimeException {

    private final transient ChatReply reply;

    public MeteredChatException(RuntimeException cause, ChatReply reply) {
        super(cause.getMessage(), cause);
        this.reply = reply;
    }

    public ChatReply reply() {
        return reply;
    }

    public RuntimeException original() {
        return (RuntimeException) getCause();
    }
}
