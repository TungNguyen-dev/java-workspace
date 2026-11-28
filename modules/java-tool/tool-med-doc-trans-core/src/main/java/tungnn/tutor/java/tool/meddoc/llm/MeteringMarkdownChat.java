package tungnn.tutor.java.tool.meddoc.llm;

import java.util.Objects;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

public final class MeteringMarkdownChat implements MarkdownChat {

    private final MarkdownChat delegate;
    private final ChatUsage usage;

    public MeteringMarkdownChat(MarkdownChat delegate, ChatUsage usage) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.usage = Objects.requireNonNull(usage, "usage must not be null");
    }

    public MarkdownChat delegate() {
        return delegate;
    }

    @Override
    public String chat(String system, String user) {
        try {
            if (delegate instanceof MeteredChat metered) {
                var reply = metered.chatWithUsage(system, user);
                usage.record(reply);
                return reply.text();
            }
            var text = delegate.chat(system, user);
            usage.recordUnreported();
            return text;
        } catch (MeteredChatException e) {
            usage.recordFailure(e.reply());
            throw e.original();
        } catch (RuntimeException | Error e) {
            usage.recordFailure();
            throw e;
        }
    }
}
