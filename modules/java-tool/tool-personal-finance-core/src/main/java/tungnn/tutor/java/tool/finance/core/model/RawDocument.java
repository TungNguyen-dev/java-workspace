package tungnn.tutor.java.tool.finance.core.model;

import java.time.Instant;
import java.util.Objects;

public record RawDocument(
        String sourceId, SourceKind kind, String origin, String mediaType, String text, byte[] bytes, Instant receivedAt) {

    public RawDocument {
        Objects.requireNonNull(sourceId, "sourceId must not be null");
        Objects.requireNonNull(kind, "kind must not be null");
        Objects.requireNonNull(origin, "origin must not be null");
        Objects.requireNonNull(mediaType, "mediaType must not be null");
    }
}
