package tungnn.tutor.java.core.lib.jdbc.extractor;

import java.sql.Connection;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.MetadataExtractStrategy.ExtractionOptions;

public interface MetadataExtractor {

  /** Trích xuất metadata sử dụng các tùy chọn mặc định. */
  DatabaseSchema extract(Connection connection, String catalog, String schema);

  /** Trích xuất metadata với các tùy chọn cấu hình nâng cao. */
  default DatabaseSchema extract(
      Connection connection, String catalog, String schema, ExtractionOptions options) {
    return extract(connection, catalog, schema);
  }
}
