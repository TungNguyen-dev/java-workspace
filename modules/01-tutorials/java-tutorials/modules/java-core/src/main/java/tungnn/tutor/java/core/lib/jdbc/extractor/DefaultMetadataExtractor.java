package tungnn.tutor.java.core.lib.jdbc.extractor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.MetadataExtractStrategy;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.MetadataExtractStrategy.ExtractionOptions;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.OracleMetadataExtractStrategy;
import tungnn.tutor.java.core.lib.jdbc.extractor.strategy.PostgresMetadataExtractStrategy;

public class DefaultMetadataExtractor implements MetadataExtractor {

  private final List<MetadataExtractStrategy> strategies = new CopyOnWriteArrayList<>();

  public DefaultMetadataExtractor() {
    this.strategies.add(new PostgresMetadataExtractStrategy());
    this.strategies.add(new OracleMetadataExtractStrategy());
  }

  public DefaultMetadataExtractor(List<MetadataExtractStrategy> customStrategies) {
    if (customStrategies != null && !customStrategies.isEmpty()) {
      this.strategies.addAll(customStrategies);
    }
  }

  /** Đăng ký thêm Strategy mới. Strategy mới đăng ký sẽ được ưu tiên kiểm tra trước. */
  public void registerStrategy(MetadataExtractStrategy strategy) {
    if (strategy != null) {
      // Đưa strategy mới lên đầu danh sách để ưu tiên đè strategy mặc định
      this.strategies.addFirst(strategy);
    }
  }

  @Override
  public DatabaseSchema extract(Connection connection, String catalog, String schema) {
    return extract(connection, catalog, schema, ExtractionOptions.defaultOptions());
  }

  @Override
  public DatabaseSchema extract(
      Connection connection, String catalog, String schema, ExtractionOptions options) {
    Objects.requireNonNull(connection, "Connection must not be null");
    ExtractionOptions effectiveOptions =
        options != null ? options : ExtractionOptions.defaultOptions();

    try {
      DatabaseMetaData metaData = connection.getMetaData();
      String dbProductName = metaData.getDatabaseProductName();

      MetadataExtractStrategy strategy = resolveStrategy(dbProductName);

      return strategy.extract(connection, catalog, schema, effectiveOptions);

    } catch (SQLException e) {
      throw new RuntimeException("Failed to extract database metadata: " + e.getMessage(), e);
    }
  }

  private MetadataExtractStrategy resolveStrategy(String dbProductName) {
    return strategies.stream()
        .filter(s -> s.supports(dbProductName))
        .findFirst()
        .orElseThrow(
            () ->
                new UnsupportedOperationException(
                    "No MetadataExtractStrategy found for database engine: " + dbProductName));
  }
}
