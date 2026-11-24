package tungnn.tutor.java.core.lib.jdbc.extractor;

import java.sql.Connection;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.MetadataExtractStrategy.ExtractionOptions;

public interface MetadataExtractor {

  DatabaseSchema extract(Connection connection, String catalog, String schema);

  default DatabaseSchema extract(
      Connection connection, String catalog, String schema, ExtractionOptions options) {
    return extract(connection, catalog, schema);
  }
}
