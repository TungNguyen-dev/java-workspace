package tungnn.tutor.java.tool.todo.core.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public record TaskRef(String path, int line, String hash) {

    public static TaskRef of(String path, int line, String rawLine) {
        return new TaskRef(path, line, hashOf(rawLine));
    }

    public static TaskRef parse(String id) {
        var hashAt = id.lastIndexOf('#');
        var lineAt = hashAt < 0 ? -1 : id.lastIndexOf(':', hashAt);
        if (hashAt < 0 || lineAt <= 0) {
            throw new IllegalArgumentException("Invalid task id: " + id + " (expected path:line#hash)");
        }
        try {
            return new TaskRef(
                    id.substring(0, lineAt),
                    Integer.parseInt(id.substring(lineAt + 1, hashAt)),
                    id.substring(hashAt + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task id: " + id + " (line is not a number)", e);
        }
    }

    public static String hashOf(String rawLine) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(rawLine.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 3);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    @Override
    public String toString() {
        return path + ":" + line + "#" + hash;
    }
}
