package tungnn.tutor.java.docker.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ContainerSpec(
        String image,
        String name,
        List<String> cmd,
        Map<String, String> env,
        Map<Integer, Integer> ports,
        List<String> binds,
        Map<String, String> labels,
        String network,
        boolean autoRemove) {

    public ContainerSpec {
        Objects.requireNonNull(image, "image must not be null");
        cmd = cmd == null ? List.of() : List.copyOf(cmd);
        env = env == null ? Map.of() : Map.copyOf(env);
        ports = ports == null ? Map.of() : Map.copyOf(ports);
        binds = binds == null ? List.of() : List.copyOf(binds);
        labels = labels == null ? Map.of() : Map.copyOf(labels);
    }

    public static Builder builder(String image) {
        return new Builder(image);
    }

    public static final class Builder {
        private final String image;
        private final List<String> cmd = new ArrayList<>();
        private final Map<String, String> env = new LinkedHashMap<>();
        private final Map<Integer, Integer> ports = new LinkedHashMap<>();
        private final List<String> binds = new ArrayList<>();
        private final Map<String, String> labels = new LinkedHashMap<>();
        private String name;
        private String network;
        private boolean autoRemove;

        private Builder(String image) {
            this.image = image;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder cmd(String... command) {
            cmd.addAll(List.of(command));
            return this;
        }

        public Builder env(String key, String value) {
            env.put(key, value);
            return this;
        }

        public Builder port(int hostPort, int containerPort) {
            ports.put(hostPort, containerPort);
            return this;
        }

        public Builder bind(String hostPath, String containerPath) {
            binds.add(hostPath + ":" + containerPath);
            return this;
        }

        public Builder label(String key, String value) {
            labels.put(key, value);
            return this;
        }

        public Builder network(String network) {
            this.network = network;
            return this;
        }

        public Builder autoRemove(boolean autoRemove) {
            this.autoRemove = autoRemove;
            return this;
        }

        public ContainerSpec build() {
            return new ContainerSpec(image, name, cmd, env, ports, binds, labels, network, autoRemove);
        }
    }
}
