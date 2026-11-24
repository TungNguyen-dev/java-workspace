package tungnn.tutor.java.tool.hub.app.web;

import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.tool.hub.api.ToolFile;

@Component
public class DownloadStore {
    private static final int MAX_FILES_PER_OWNER = 10;
    private static final long MAX_TOTAL_BYTES = 200L * 1024 * 1024;
    private static final Duration TTL = Duration.ofMinutes(30);
    private final Map<String, Stored> files = new LinkedHashMap<>();
    private long totalBytes;

    private record Stored(String owner, ToolFile file, long expiresAtMs) {}

    public synchronized String put(String owner, ToolFile file) {
        evictExpired();
        String token = UUID.randomUUID().toString();
        files.put(token, new Stored(owner, file, System.currentTimeMillis() + TTL.toMillis()));
        totalBytes += file.size();
        evictOwnerOverflow(owner);
        while (totalBytes > MAX_TOTAL_BYTES && files.size() > 1) removeEldest();
        return token;
    }

    public synchronized Optional<ToolFile> get(String owner, String token) {
        evictExpired();
        Stored s = files.get(token);
        if (s == null || !s.owner().equals(owner)) return Optional.empty();
        return Optional.of(s.file());
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        for (Iterator<Stored> it = files.values().iterator(); it.hasNext(); ) {
            Stored s = it.next();
            if (s.expiresAtMs() < now) {
                totalBytes -= s.file().size();
                it.remove();
            }
        }
    }

    private void evictOwnerOverflow(String owner) {
        while (files.values().stream().filter(s -> s.owner().equals(owner)).count() > MAX_FILES_PER_OWNER) {
            for (Iterator<Stored> it = files.values().iterator(); it.hasNext(); ) {
                Stored s = it.next();
                if (s.owner().equals(owner)) {
                    totalBytes -= s.file().size();
                    it.remove();
                    break;
                }
            }
        }
    }

    private void removeEldest() {
        Iterator<Stored> it = files.values().iterator();
        totalBytes -= it.next().file().size();
        it.remove();
    }
}
