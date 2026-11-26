package tungnn.tutor.java.tool.meddoc.core.llm;

import java.util.Objects;

public record StepChat(MarkdownChat chat, Integer maxConcurrency, boolean claude) {
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
