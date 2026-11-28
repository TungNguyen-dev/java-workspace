package tungnn.tutor.java.tool.meddoc.llm;

import java.util.Objects;
import tungnn.tutor.java.ai.chat.TextChat;

public record StepChat(TextChat chat, Integer maxConcurrency, boolean claude, String label) {
    public StepChat(TextChat chat, Integer maxConcurrency, boolean claude) {
        this(chat, maxConcurrency, claude, null);
    }

    public StepChat {
        Objects.requireNonNull(chat, "chat must not be null");
    }

    public static StepChat of(TextChat chat) {
        return new StepChat(chat, null, false);
    }

    public static StepChat of(TextChat chat, boolean claude) {
        return new StepChat(chat, null, claude);
    }
}
