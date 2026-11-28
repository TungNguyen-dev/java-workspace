package tungnn.tutor.java.tool.meddoc.llm;

import java.util.Objects;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

public record StepChat(MarkdownChat chat, Integer maxConcurrency, boolean claude, String label) {
    public StepChat(MarkdownChat chat, Integer maxConcurrency, boolean claude) {
        this(chat, maxConcurrency, claude, null);
    }

    public StepChat {
        Objects.requireNonNull(chat, "chat must not be null");
    }

    public static StepChat of(MarkdownChat chat) {
        return new StepChat(chat, null, false);
    }

    public static StepChat of(MarkdownChat chat, boolean claude) {
        return new StepChat(chat, null, claude);
    }
}
