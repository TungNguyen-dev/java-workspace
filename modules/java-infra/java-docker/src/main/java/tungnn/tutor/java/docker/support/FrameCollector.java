package tungnn.tutor.java.docker.support;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class FrameCollector extends ResultCallback.Adapter<Frame> {
    private final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private final ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    @Override
    public void onNext(Frame frame) {
        var payload = frame.getPayload();
        if (payload == null) {
            return;
        }
        // RAW frames come from TTY sessions where stdout and stderr are merged
        if (frame.getStreamType() == StreamType.STDERR) {
            stderr.write(payload, 0, payload.length);
        } else {
            stdout.write(payload, 0, payload.length);
        }
    }

    public String stdout() {
        return stdout.toString(StandardCharsets.UTF_8);
    }

    public String stderr() {
        return stderr.toString(StandardCharsets.UTF_8);
    }
}
