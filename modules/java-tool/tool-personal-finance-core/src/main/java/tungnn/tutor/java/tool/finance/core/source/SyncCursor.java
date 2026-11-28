package tungnn.tutor.java.tool.finance.core.source;

import java.time.Instant;

public record SyncCursor(Instant lastSyncedAt) {

    public static SyncCursor initial() {
        return new SyncCursor(Instant.EPOCH);
    }
}
