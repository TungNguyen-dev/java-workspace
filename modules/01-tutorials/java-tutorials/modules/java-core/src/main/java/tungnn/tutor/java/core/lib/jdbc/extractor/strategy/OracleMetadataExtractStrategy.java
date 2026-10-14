package tungnn.tutor.java.core.lib.jdbc.extractor.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;

/** Oracle Metadata Extraction Strategy. */
public class OracleMetadataExtractStrategy extends AbstractMetadataExtractStrategy {

  @Override
  public boolean supports(String databaseType) {
    if (databaseType == null || databaseType.isBlank()) {
      return false;
    }
    return databaseType.equalsIgnoreCase("Oracle")
        || databaseType.toLowerCase(Locale.ROOT).contains("oracle");
  }

  // =========================================================================
  // 1. EXTRACT TABLES & CONSTRAINTS
  // =========================================================================

  @Override
  protected Map<String, DatabaseSchema.TableModel> extractTables(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String targetSchema = (schema != null) ? schema.toUpperCase(Locale.ROOT) : "";
    List<String> whitelist = normalizeWhitelist(options.tableWhiteList());
    boolean hasWhitelist = !whitelist.isEmpty();

    Map<String, TableHolder> tables = new LinkedHashMap<>();

    // 1.1 Query Table Comments
    StringBuilder sqlTables =
        new StringBuilder(
            """
                    SELECT t.table_name, tc.comments
                    FROM all_tables t
                    LEFT JOIN all_tab_comments tc
                           ON tc.owner = t.owner AND tc.table_name = t.table_name
                    WHERE t.owner = ?
                   \s""");

    if (hasWhitelist) {
      sqlTables
          .append(" AND t.table_name IN (")
          .append(String.join(",", Collections.nCopies(whitelist.size(), "?")))
          .append(")");
    }
    sqlTables.append(" ORDER BY t.table_name");

    try (PreparedStatement pstmt = connection.prepareStatement(sqlTables.toString())) {
      int paramIdx = 1;
      pstmt.setString(paramIdx++, targetSchema);
      if (hasWhitelist) {
        for (String tbl : whitelist) {
          pstmt.setString(paramIdx++, tbl);
        }
      }

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.computeIfAbsent(tableName, TableHolder::new);
          holder.comment = getStringOrNull(rs, "comments");
        }
      }
    }

    if (tables.isEmpty()) {
      return Map.of();
    }

    // 1.2 Query Columns
    StringBuilder sqlColumns =
        new StringBuilder(
            """
                    SELECT c.table_name, c.column_name, c.data_type,
                           c.data_length, c.data_precision, c.data_scale,
                           c.nullable, c.data_default, c.identity_column, c.column_id,
                           cc.comments
                    FROM all_tab_cols c
                    LEFT JOIN all_col_comments cc
                           ON cc.owner = c.owner AND cc.table_name = c.table_name AND cc.column_name = c.column_name
                    WHERE c.owner = ? AND c.user_generated = 'YES'
                    """);

    if (hasWhitelist) {
      sqlColumns
          .append(" AND c.table_name IN (")
          .append(String.join(",", Collections.nCopies(whitelist.size(), "?")))
          .append(")");
    }
    sqlColumns.append(" ORDER BY c.table_name, c.column_id");

    try (PreparedStatement pstmt = connection.prepareStatement(sqlColumns.toString())) {
      int paramIdx = 1;
      pstmt.setString(paramIdx++, targetSchema);
      if (hasWhitelist) {
        for (String tbl : whitelist) {
          pstmt.setString(paramIdx++, tbl);
        }
      }

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.get(tableName);
          if (holder == null) continue;

          String rawType = rs.getString("data_type");
          String colDefault = getStringOrNull(rs, "data_default");
          boolean isIdentity = "YES".equalsIgnoreCase(rs.getString("identity_column"));

          DatabaseSchema.ColumnModel col =
              new DatabaseSchema.ColumnModel(
                  rs.getString("column_name"),
                  normalizeType(rawType),
                  rawType,
                  getStringOrNull(rs, "data_length"),
                  getStringOrNull(rs, "data_precision"),
                  getStringOrNull(rs, "data_scale"),
                  "Y".equalsIgnoreCase(rs.getString("nullable")),
                  colDefault,
                  isIdentity,
                  getStringOrNull(rs, "column_id"),
                  getStringOrNull(rs, "comments"));
          holder.columns.put(col.name(), col);
        }
      }
    }

    fetchConstraints(connection, targetSchema, tables);
    fetchIndexes(connection, targetSchema, tables);

    // Chuyển đổi từ TableHolder sang TableModel (Tránh dùng Lambda gây unhandled exception)
    Map<String, DatabaseSchema.TableModel> result = new LinkedHashMap<>();
    for (Map.Entry<String, TableHolder> entry : tables.entrySet()) {
      String tableName = entry.getKey();
      TableHolder holder = entry.getValue();

      // Convert Primary Key
      DatabaseSchema.PrimaryKeyModel pkModel = null;
      if (holder.primaryKeyHolder != null) {
        pkModel =
            new DatabaseSchema.PrimaryKeyModel(
                holder.primaryKeyHolder.name, holder.primaryKeyHolder.columns);
      }

      // Convert Unique Keys
      Map<String, DatabaseSchema.UniqueKeyModel> uniqueKeys = new LinkedHashMap<>();
      holder.uniqueKeyHolders.forEach(
          (k, v) -> uniqueKeys.put(k, new DatabaseSchema.UniqueKeyModel(v.name, v.columns)));

      // Convert Foreign Keys
      Map<String, DatabaseSchema.ForeignKeyModel> foreignKeys = new LinkedHashMap<>();
      holder.foreignKeyHolders.forEach(
          (k, v) ->
              foreignKeys.put(
                  k,
                  new DatabaseSchema.ForeignKeyModel(
                      v.name,
                      v.columns,
                      v.foreignOwner,
                      v.foreignTable,
                      v.foreignColumns,
                      v.deleteRule,
                      v.updateRule,
                      "")));

      // Convert Indexes
      Map<String, DatabaseSchema.IndexModel> indices = new LinkedHashMap<>();
      holder.indexHolders.forEach(
          (k, v) ->
              indices.put(
                  k, new DatabaseSchema.IndexModel(v.name, v.type, v.isUnique, v.columns, "")));

      result.put(
          tableName,
          new DatabaseSchema.TableModel(
              tableName,
              "",
              "",
              "",
              holder.comment,
              holder.columns,
              pkModel,
              foreignKeys,
              uniqueKeys,
              holder.checkConstraints,
              indices));
    }

    return result;
  }

  private void fetchConstraints(Connection conn, String schema, Map<String, TableHolder> tables)
      throws SQLException {
    String sql =
        """
                SELECT c.search_condition,
                       c.constraint_name, c.constraint_type, c.table_name,
                       c.r_owner AS foreign_owner, r_c.table_name AS foreign_table_name,
                       c.delete_rule,
                       cc.column_name, r_cc.column_name AS foreign_column_name
                FROM all_constraints c
                LEFT JOIN all_constraints r_c ON c.r_owner = r_c.owner AND c.r_constraint_name = r_c.constraint_name
                LEFT JOIN all_cons_columns cc ON c.owner = cc.owner AND c.constraint_name = cc.constraint_name
                LEFT JOIN all_cons_columns r_cc ON r_c.owner = r_cc.owner AND r_c.constraint_name = r_cc.constraint_name AND cc.position = r_cc.position
                WHERE c.owner = ? AND c.constraint_type IN ('P', 'U', 'R', 'C')
                ORDER BY c.constraint_name, cc.position
                """;

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          // QUAN TRỌNG: Phải đọc cột kiểu LONG (search_condition) ĐẦU TIÊN trước các cột khác
          String condition = getStringOrNull(rs, "search_condition");

          String tableName = rs.getString("table_name");
          TableHolder holder = tables.get(tableName);
          if (holder == null) continue;

          String constraintName = rs.getString("constraint_name");
          String conType = rs.getString("constraint_type");
          String colName = rs.getString("column_name");

          switch (conType) {
            case "P" -> {
              if (holder.primaryKeyHolder == null) {
                holder.primaryKeyHolder = new ConstraintHolder(constraintName);
              }
              if (colName != null) holder.primaryKeyHolder.columns.add(colName);
            }
            case "U" -> {
              ConstraintHolder uk =
                  holder.uniqueKeyHolders.computeIfAbsent(constraintName, ConstraintHolder::new);
              if (colName != null) uk.columns.add(colName);
            }
            case "R" -> {
              ForeignKeyHolder fk =
                  holder.foreignKeyHolders.computeIfAbsent(
                      constraintName,
                      k -> {
                        ForeignKeyHolder fkh = new ForeignKeyHolder(k);
                        try {
                          fkh.foreignOwner = getStringOrNull(rs, "foreign_owner");
                          fkh.foreignTable = getStringOrNull(rs, "foreign_table_name");
                          fkh.deleteRule = mapOracleDeleteRule(rs.getString("delete_rule"));
                        } catch (SQLException ignored) {
                        }
                        return fkh;
                      });
              if (colName != null) fk.columns.add(colName);
              String fCol = rs.getString("foreign_column_name");
              if (fCol != null) fk.foreignColumns.add(fCol);
            }
            case "C" -> {
              if (condition != null
                  && !condition.toUpperCase(Locale.ROOT).endsWith("IS NOT NULL")) {
                holder.checkConstraints.put(
                    constraintName,
                    new DatabaseSchema.CheckConstraintModel(constraintName, condition));
              }
            }
          }
        }
      }
    }
  }

  private void fetchIndexes(Connection conn, String schema, Map<String, TableHolder> tables)
      throws SQLException {
    String sql =
        """
            SELECT i.index_name, i.table_name, i.uniqueness, i.index_type,
                   ic.column_name, ic.descend
            FROM all_indexes i
            JOIN all_ind_columns ic ON i.owner = ic.index_owner AND i.index_name = ic.index_name
            WHERE i.owner = ? AND i.generated = 'N'
            ORDER BY i.index_name, ic.column_position
            """;

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.get(tableName);
          if (holder == null) continue;

          String indexName = rs.getString("index_name");
          boolean isUnique = "UNIQUE".equalsIgnoreCase(rs.getString("uniqueness"));
          String indexType = rs.getString("index_type");
          String colName = rs.getString("column_name");
          String order = rs.getString("descend");

          IndexHolder index =
              holder.indexHolders.computeIfAbsent(
                  indexName, k -> new IndexHolder(k, normalizeType(indexType), isUnique));

          if (colName != null) {
            index.columns.add(new DatabaseSchema.IndexModel.IndexColumn(colName, order));
          }
        }
      }
    }
  }

  // =========================================================================
  // 2. EXTRACT VIEWS
  // =========================================================================

  @Override
  protected Map<String, DatabaseSchema.ViewModel> extractViews(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String targetSchema = (schema != null) ? schema.toUpperCase(Locale.ROOT) : "";
    String sql =
        """
            SELECT v.view_name, v.text AS view_definition, vc.comments
            FROM all_views v
            LEFT JOIN all_tab_comments vc
                   ON vc.owner = v.owner AND vc.table_name = v.view_name
            WHERE v.owner = ?
            """;

    Map<String, DatabaseSchema.ViewModel> views = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, targetSchema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String viewName = rs.getString("view_name");
          views.put(
              viewName,
              new DatabaseSchema.ViewModel(
                  viewName,
                  getStringOrNull(rs, "view_definition"),
                  getStringOrNull(rs, "comments")));
        }
      }
    }
    return views;
  }

  // =========================================================================
  // 3. EXTRACT PROCEDURES & FUNCTIONS
  // =========================================================================

  @Override
  protected Map<String, DatabaseSchema.RoutineModel> extractProcedures(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {
    return extractRoutines(connection, schema, "PROCEDURE");
  }

  @Override
  protected Map<String, DatabaseSchema.RoutineModel> extractFunctions(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {
    return extractRoutines(connection, schema, "FUNCTION");
  }

  private Map<String, DatabaseSchema.RoutineModel> extractRoutines(
      Connection conn, String schema, String routineType) throws SQLException {

    String targetSchema = (schema != null) ? schema.toUpperCase(Locale.ROOT) : "";
    String sql =
        """
            SELECT object_name
            FROM all_objects
            WHERE owner = ? AND object_type = ?
            ORDER BY object_name
            """;

    Map<String, DatabaseSchema.RoutineModel> routines = new LinkedHashMap<>();
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, targetSchema);
      pstmt.setString(2, routineType);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String name = rs.getString("object_name");
          routines.put(
              name,
              new DatabaseSchema.RoutineModel(
                  name, routineType, List.of(), "VOID", "VOID", false, "", ""));
        }
      }
    }
    return routines;
  }

  // =========================================================================
  // 4. EXTRACT TRIGGERS, SEQUENCES & CUSTOM TYPES
  // =========================================================================

  @Override
  protected Map<String, DatabaseSchema.TriggerModel> extractTriggers(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String targetSchema = (schema != null) ? schema.toUpperCase(Locale.ROOT) : "";
    String sql =
        """
            SELECT trigger_name, table_name, triggering_event, trigger_type, trigger_body
            FROM all_triggers
            WHERE owner = ?
            """;

    Map<String, DatabaseSchema.TriggerModel> triggers = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, targetSchema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String name = rs.getString("trigger_name");
          triggers.put(
              name,
              new DatabaseSchema.TriggerModel(
                  name,
                  getStringOrNull(rs, "table_name"),
                  getStringOrNull(rs, "triggering_event"),
                  getStringOrNull(rs, "trigger_type"),
                  getStringOrNull(rs, "trigger_body"),
                  ""));
        }
      }
    }
    return triggers;
  }

  @Override
  protected Map<String, DatabaseSchema.SequenceModel> extractSequences(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String targetSchema = (schema != null) ? schema.toUpperCase(Locale.ROOT) : "";
    String sql =
        """
            SELECT sequence_name, min_value, max_value, increment_by, cycle_flag, last_number
            FROM all_sequences
            WHERE sequence_owner = ?
            """;

    Map<String, DatabaseSchema.SequenceModel> sequences = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, targetSchema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String seqName = rs.getString("sequence_name");
          sequences.put(
              seqName,
              new DatabaseSchema.SequenceModel(
                  seqName,
                  "NUMBER",
                  getStringOrNull(rs, "last_number"),
                  getStringOrNull(rs, "increment_by"),
                  getStringOrNull(rs, "min_value"),
                  getStringOrNull(rs, "max_value"),
                  "Y".equalsIgnoreCase(rs.getString("cycle_flag")),
                  ""));
        }
      }
    }
    return sequences;
  }

  @Override
  protected Map<String, DatabaseSchema.CustomTypeModel> extractCustomTypes(
      Connection connection, String catalog, String schema, ExtractionOptions options) {
    return Map.of();
  }

  // =========================================================================
  // HELPER METHODS & INNER CLASSES
  // =========================================================================

  private String mapOracleDeleteRule(String rule) {
    if (rule == null) return "NO ACTION";
    return switch (rule.toUpperCase(Locale.ROOT)) {
      case "CASCADE" -> "CASCADE";
      case "SET NULL" -> "SET NULL";
      default -> "NO ACTION";
    };
  }

  // Lớp tạm lưu trữ dữ liệu mutable trước khi tạo Record / Model immutable
  private static class ConstraintHolder {
    final String name;
    final List<String> columns = new ArrayList<>();

    ConstraintHolder(String name) {
      this.name = name;
    }
  }

  private static class ForeignKeyHolder {
    final String name;
    final List<String> columns = new ArrayList<>();
    final List<String> foreignColumns = new ArrayList<>();
    String foreignOwner;
    String foreignTable;
    String deleteRule = "NO ACTION";
    String updateRule = "NO ACTION";

    ForeignKeyHolder(String name) {
      this.name = name;
    }
  }

  private static class IndexHolder {
    final String name;
    final String type;
    final boolean isUnique;
    final List<DatabaseSchema.IndexModel.IndexColumn> columns = new ArrayList<>();

    IndexHolder(String name, String type, boolean isUnique) {
      this.name = name;
      this.type = type;
      this.isUnique = isUnique;
    }
  }

  private static class TableHolder {
    final String tableName;
    final Map<String, DatabaseSchema.ColumnModel> columns = new LinkedHashMap<>();
    final Map<String, ConstraintHolder> uniqueKeyHolders = new LinkedHashMap<>();
    final Map<String, ForeignKeyHolder> foreignKeyHolders = new LinkedHashMap<>();
    final Map<String, DatabaseSchema.CheckConstraintModel> checkConstraints = new LinkedHashMap<>();
    final Map<String, IndexHolder> indexHolders = new LinkedHashMap<>();
    ConstraintHolder primaryKeyHolder;
    String comment = "";

    TableHolder(String tableName) {
      this.tableName = tableName;
    }
  }
}
