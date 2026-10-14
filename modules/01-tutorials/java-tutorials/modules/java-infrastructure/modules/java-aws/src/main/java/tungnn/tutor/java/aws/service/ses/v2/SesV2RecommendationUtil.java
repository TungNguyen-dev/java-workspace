// SesV2RecommendationUtil.java
package tungnn.tutor.java.aws.service.ses.v2;

import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

public final class SesV2RecommendationUtil {

  private SesV2RecommendationUtil() {}

  public static List<Recommendation> listRecommendations(
      SesV2Client client, ListRecommendationsRequest request) {
    return client.listRecommendationsPaginator(request).stream()
        .flatMap(r -> r.recommendations().stream())
        .collect(Collectors.toList());
  }
}
