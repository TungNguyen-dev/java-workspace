package tungnn.tutor.java.core.lib.jdbc.extractor.strategy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;

/**
 * Abstract Strategy đóng vai trò khung thực thi (Template Method Pattern) cho việc trích xuất
 * metadata.
 *
 * <p>Thực thi tuần tự các tác vụ trích xuất trên cùng một JDBC Connection để đảm bảo an toàn bộ nhớ
 * và trạng thái kết nối (Thread-Safety).
 */
public abstract class AbstractMetadataExtractStrategy implements MetadataExtractStrategy {

  @Override
  public DatabaseSchema extract(
      Connection connection, String catalog, String schema, ExtractionOptions options) {

    ExtractionOptions effectiveOptions =
        (options != null) ? options : ExtractionOptions.defaultOptions();

    try {
      if (connection == null || connection.isClosed()) {
        throw new IllegalArgumentException("JDBC Connection must be open and non-null.");
      }

      DatabaseMetaData dbMetaData = connection.getMetaData();
      String engineName = dbMetaData.getDatabaseProductName();
      String engineVersion = dbMetaData.getDatabaseProductVersion();

      // Fallback catalog / schema từ connection nếu không truyền vào
      String resolvedCatalog = resolveCatalog(connection, catalog);
      String resolvedSchema = resolveSchema(connection, schema);

      DatabaseSchema.SchemaMeta meta =
          new DatabaseSchema.SchemaMeta(
              resolvedCatalog, resolvedSchema, engineName, engineVersion, Instant.now());

      // Executing sequentially to prevent concurrent access issues on JDBC Connection
      Map<String, DatabaseSchema.TableModel> tables =
          effectiveOptions.includeTables()
              ? safeExtract(
                  "Tables",
                  () ->
                      extractTables(connection, resolvedCatalog, resolvedSchema, effectiveOptions))
              : Map.of();

      Map<String, DatabaseSchema.ViewModel> views =
          effectiveOptions.includeViews()
              ? safeExtract(
                  "Views",
                  () -> extractViews(connection, resolvedCatalog, resolvedSchema, effectiveOptions))
              : Map.of();

      Map<String, DatabaseSchema.RoutineModel> procedures =
          effectiveOptions.includeProcedures()
              ? safeExtract(
                  "Procedures",
                  () ->
                      extractProcedures(
                          connection, resolvedCatalog, resolvedSchema, effectiveOptions))
              : Map.of();

      Map<String, DatabaseSchema.RoutineModel> functions =
          effectiveOptions.includeProcedures()
              ? safeExtract(
                  "Functions",
                  () ->
                      extractFunctions(
                          connection, resolvedCatalog, resolvedSchema, effectiveOptions))
              : Map.of();

      Map<String, DatabaseSchema.TriggerModel> triggers =
          effectiveOptions.includeTriggers()
              ? safeExtract(
                  "Triggers",
                  () ->
                      extractTriggers(
                          connection, resolvedCatalog, resolvedSchema, effectiveOptions))
              : Map.of();

      Map<String, DatabaseSchema.SequenceModel> sequences =
          safeExtract(
              "Sequences",
              () ->
                  extractSequences(connection, resolvedCatalog, resolvedSchema, effectiveOptions));

      Map<String, DatabaseSchema.CustomTypeModel> customTypes =
          safeExtract(
              "CustomTypes",
              () ->
                  extractCustomTypes(
                      connection, resolvedCatalog, resolvedSchema, effectiveOptions));

      return new DatabaseSchema(
          meta, tables, views, procedures, functions, triggers, sequences, customTypes);

    } catch (SQLException e) {
      throw new MetadataExtractionException(
          "Failed to access database metadata: " + e.getMessage(), e);
    }
  }

  // =========================================================================
  // Template Methods (Cần được override bởi concrete strategy như Postgres, MySQL)
  // =========================================================================

  protected abstract Map<String, DatabaseSchema.TableModel> extractTables(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.ViewModel> extractViews(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.RoutineModel> extractProcedures(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.RoutineModel> extractFunctions(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.TriggerModel> extractTriggers(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.SequenceModel> extractSequences(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  protected abstract Map<String, DatabaseSchema.CustomTypeModel> extractCustomTypes(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException;

  // =========================================================================
  // Internal Helper Methods
  // =========================================================================

  private <T> T safeExtract(String taskName, SqlSupplier<T> supplier) {
    try {
      T result = supplier.get();
      if (result != null) {
        return result;
      }
      @SuppressWarnings("unchecked")
      T emptyMap = (T) Map.of();
      return emptyMap;
    } catch (SQLException e) {
      throw new MetadataExtractionException(
          "Failed to extract " + taskName + ": " + e.getMessage(), e);
    }
  }

  private String resolveCatalog(Connection connection, String catalog) {
    if (catalog != null && !catalog.isBlank()) {
      return catalog;
    }
    try {
      return connection.getCatalog();
    } catch (SQLException ignored) {
      return null;
    }
  }

  private String resolveSchema(Connection connection, String schema) {
    if (schema != null && !schema.isBlank()) {
      return schema;
    }
    try {
      return connection.getSchema();
    } catch (SQLException ignored) {
      return null;
    }
  }

  protected List<String> normalizeWhitelist(List<String> list) {
    if (list == null || list.isEmpty()) return List.of();
    List<String> res = new ArrayList<>();
    for (String s : list) {
      if (s != null && !s.isBlank()) {
        res.add(s.toUpperCase(Locale.ROOT));
      }
    }
    return res;
  }

  // =========================================================================
  // Custom Exception
  // =========================================================================

  protected String getStringOrNull(ResultSet rs, String colName) throws SQLException {
    String val = rs.getString(colName);
    return rs.wasNull() ? null : val;
  }

  protected String normalizeType(String rawType) {
    if (rawType == null || rawType.isBlank()) return "UNKNOWN";
    return rawType.toUpperCase(Locale.ROOT).trim();
  }

  @FunctionalInterface
  private interface SqlSupplier<T> {
    T get() throws SQLException;
  }

  public static class MetadataExtractionException extends RuntimeException {
    public MetadataExtractionException(String message, Throwable cause) {
      super(message, cause);
    }

    public MetadataExtractionException(String message) {
      super(message);
    }
  }
}
