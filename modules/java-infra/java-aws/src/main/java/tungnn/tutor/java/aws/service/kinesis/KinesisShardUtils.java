package tungnn.tutor.java.aws.service.kinesis;

import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.*;

public final class KinesisShardUtils {
    private KinesisShardUtils() {}

    public static ListShardsResponse listShards(KinesisClient client, ListShardsRequest request) {
        return client.listShards(request);
    }

    public static MergeShardsResponse mergeShards(KinesisClient client, MergeShardsRequest request) {
        return client.mergeShards(request);
    }

    public static SplitShardResponse splitShard(KinesisClient client, SplitShardRequest request) {
        return client.splitShard(request);
    }

    public static UpdateShardCountResponse updateShardCount(KinesisClient client, UpdateShardCountRequest request) {
        return client.updateShardCount(request);
    }
}
