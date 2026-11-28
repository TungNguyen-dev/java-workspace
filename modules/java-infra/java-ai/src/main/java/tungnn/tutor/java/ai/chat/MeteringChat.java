package tungnn.tutor.java.ai.chat;

import java.util.Objects;

public final class MeteringChat implements TextChat {

    private final TextChat delegate;
    private final ChatUsage usage;

    public MeteringChat(TextChat delegate, ChatUsage usage) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        this.usage = Objects.requireNonNull(usage, "usage must not be null");
    }

    public TextChat delegate() {
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
