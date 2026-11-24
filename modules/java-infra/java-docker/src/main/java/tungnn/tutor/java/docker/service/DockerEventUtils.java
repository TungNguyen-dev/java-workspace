package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import java.io.Closeable;
import java.util.Objects;
import java.util.function.Consumer;

public final class DockerEventUtils {
    private DockerEventUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Closeable events(DockerClient client, Consumer<Event> consumer) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(consumer, "consumer must not be null");
        return client.eventsCmd().exec(new ResultCallback.Adapter<Event>() {
            @Override
            public void onNext(Event event) {
                consumer.accept(event);
            }
        });
    }
}
