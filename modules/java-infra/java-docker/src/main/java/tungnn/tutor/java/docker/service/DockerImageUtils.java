package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PruneCmd;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PruneResponse;
import com.github.dockerjava.api.model.PruneType;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import tungnn.tutor.java.docker.support.DockerAwait;

public final class DockerImageUtils {
    private DockerImageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void pull(DockerClient client, String image, Duration timeout) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(image, "image must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        var callback = client.pullImageCmd(image).exec(new ResultCallback.Adapter<PullResponseItem>());
        DockerAwait.await(callback, timeout, "pull image " + image);
    }

    public static String build(DockerClient client, File dockerfile, Set<String> tags, Duration timeout) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(dockerfile, "dockerfile must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        var cmd = client.buildImageCmd(dockerfile).withTags(tags);
        var callback = cmd.start();
        DockerAwait.await(callback, timeout, "build image " + tags);
        return callback.awaitImageId();
    }

    public static void push(DockerClient client, String image, AuthConfig auth, Duration timeout) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(image, "image must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        var cmd = client.pushImageCmd(image);
        if (auth != null) {
            cmd.withAuthConfig(auth);
        }
        var callback = cmd.exec(new ResultCallback.Adapter<PushResponseItem>());
        DockerAwait.await(callback, timeout, "push image " + image);
    }

    public static void tag(DockerClient client, String imageId, String repository, String tag) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(imageId, "imageId must not be null");
        Objects.requireNonNull(repository, "repository must not be null");
        Objects.requireNonNull(tag, "tag must not be null");
        client.tagImageCmd(imageId, repository, tag).exec();
    }

    public static InspectImageResponse inspect(DockerClient client, String image) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(image, "image must not be null");
        return client.inspectImageCmd(image).exec();
    }

    public static List<Image> list(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.listImagesCmd().exec();
    }

    public static List<Image> listDangling(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.listImagesCmd().withDanglingFilter(true).exec();
    }

    public static void remove(DockerClient client, String image, boolean force) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(image, "image must not be null");
        client.removeImageCmd(image).withForce(force).exec();
    }

    public static boolean exists(DockerClient client, String image) {
        try {
            inspect(client, image);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public static PruneResponse prune(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        PruneCmd cmd = client.pruneCmd(PruneType.IMAGES);
        return cmd.exec();
    }
}
