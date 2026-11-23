package tungnn.tutor.java.core.lib.jdbc.extractor.strategy;

import java.sql.Connection;
import java.util.List;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;

public interface MetadataExtractStrategy {

  DatabaseSchema extract(
      Connection connection, String catalog, String schema, ExtractionOptions options);

  boolean supports(String dbProductName);

  record ExtractionOptions(
      boolean includeTables,
      boolean includeViews,
      boolean includeProcedures,
      boolean includeTriggers,
      List<String> tableWhiteList) {

    public static ExtractionOptions defaultOptions() {
      return new ExtractionOptions(true, true, true, true, List.of());
    }
  }
}
