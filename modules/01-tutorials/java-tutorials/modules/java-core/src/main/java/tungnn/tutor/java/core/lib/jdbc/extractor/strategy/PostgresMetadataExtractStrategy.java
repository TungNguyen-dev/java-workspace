package tungnn.tutor.java.core.lib.jdbc.extractor.strategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;

/** PostgreSQL Metadata Extraction Strategy. */
public class PostgresMetadataExtractStrategy extends AbstractMetadataExtractStrategy {

  @Override
  public boolean supports(String databaseType) {
    if (databaseType == null || databaseType.isBlank()) {
      return false;
    }
    return databaseType.equalsIgnoreCase("PostgreSQL")
        || databaseType.toLowerCase(Locale.ROOT).contains("postgres");
  }

  // =========================================================================
  // 1. EXTRACT TABLES & CONSTRAINTS
  // =========================================================================

  @Override
  protected Map<String, DatabaseSchema.TableModel> extractTables(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    List<String> whitelist = normalizeWhitelist(options.tableWhiteList());
    boolean hasWhitelist = !whitelist.isEmpty();

    Map<String, TableHolder> tables = new LinkedHashMap<>();

    // 1.1 Query Table Comments
    StringBuilder sqlTables =
        new StringBuilder(
            """
                    SELECT t.table_name, pgd.description
                    FROM information_schema.tables t
                    LEFT JOIN pg_catalog.pg_statio_all_tables st
                           ON st.schemaname = t.table_schema AND st.relname = t.table_name
                    LEFT JOIN pg_catalog.pg_description pgd
                           ON pgd.objoid = st.relid AND pgd.objsubid = 0
                    WHERE t.table_schema = ? AND t.table_type = 'BASE TABLE'
                    """);

    if (hasWhitelist) {
      sqlTables
          .append(" AND t.table_name IN (")
          .append(String.join(",", Collections.nCopies(whitelist.size(), "?")))
          .append(")");
    }
    sqlTables.append(" ORDER BY t.table_name");

    try (PreparedStatement pstmt = connection.prepareStatement(sqlTables.toString())) {
      int paramIdx = 1;
      pstmt.setString(paramIdx++, schema);
      if (hasWhitelist) {
        for (String tbl : whitelist) {
          pstmt.setString(paramIdx++, tbl);
        }
      }

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.computeIfAbsent(tableName, TableHolder::new);
          holder.comment = getStringOrNull(rs, "description");
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
                    SELECT c.table_name, c.column_name, c.data_type, c.udt_name,
                           c.character_maximum_length, c.numeric_precision, c.numeric_scale,
                           c.is_nullable, c.column_default, c.is_identity, c.ordinal_position,
                           pgd.description
                    FROM information_schema.columns c
                    JOIN information_schema.tables t
                      ON c.table_catalog = t.table_catalog
                     AND c.table_schema = t.table_schema
                     AND c.table_name = t.table_name
                    LEFT JOIN pg_catalog.pg_statio_all_tables st
                           ON st.schemaname = c.table_schema AND st.relname = c.table_name
                    LEFT JOIN pg_catalog.pg_description pgd
                           ON pgd.objoid = st.relid AND pgd.objsubid = c.ordinal_position
                    WHERE c.table_schema = ? AND t.table_type = 'BASE TABLE'
                    """);

    if (hasWhitelist) {
      sqlColumns
          .append(" AND c.table_name IN (")
          .append(String.join(",", Collections.nCopies(whitelist.size(), "?")))
          .append(")");
    }
    sqlColumns.append(" ORDER BY c.table_name, c.ordinal_position");

    try (PreparedStatement pstmt = connection.prepareStatement(sqlColumns.toString())) {
      int paramIdx = 1;
      pstmt.setString(paramIdx++, schema);
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
          String udtName = rs.getString("udt_name");
          String displayRawType = "USER-DEFINED".equalsIgnoreCase(rawType) ? udtName : rawType;

          String colDefault = getStringOrNull(rs, "column_default");
          boolean isIdentity =
              "YES".equalsIgnoreCase(rs.getString("is_identity"))
                  || (colDefault != null
                      && colDefault.toLowerCase(Locale.ROOT).contains("nextval("));

          DatabaseSchema.ColumnModel col =
              new DatabaseSchema.ColumnModel(
                  rs.getString("column_name"),
                  normalizeType(displayRawType),
                  displayRawType,
                  getStringOrNull(rs, "character_maximum_length"),
                  getStringOrNull(rs, "numeric_precision"),
                  getStringOrNull(rs, "numeric_scale"),
                  "YES".equalsIgnoreCase(rs.getString("is_nullable")),
                  colDefault,
                  isIdentity,
                  getStringOrNull(rs, "ordinal_position"),
                  getStringOrNull(rs, "description"));
          holder.columns.put(col.name(), col);
        }
      }
    }

    fetchConstraints(connection, schema, tables);
    fetchIndexes(connection, schema, tables);

    Map<String, DatabaseSchema.TableModel> result = new LinkedHashMap<>();
    tables.forEach(
        (tableName, holder) ->
            result.put(
                tableName,
                new DatabaseSchema.TableModel(
                    tableName,
                    "",
                    "",
                    "",
                    holder.comment,
                    holder.columns,
                    holder.primaryKey,
                    holder.foreignKeys,
                    holder.uniqueKeys,
                    holder.checkConstraints,
                    holder.indices)));

    return result;
  }

  private void fetchConstraints(Connection conn, String schema, Map<String, TableHolder> tables)
      throws SQLException {
    String sql =
        """
            SELECT con.conname AS constraint_name,
                   con.contype AS constraint_type,
                   rel.relname AS table_name,
                   fnsp.nspname AS foreign_table_schema,
                   frel.relname AS foreign_table_name,
                   pg_get_constraintdef(con.oid) AS constraint_def,
                   con.confdeltype AS delete_rule,
                   con.confupdtype AS update_rule,
                   (
                     SELECT array_agg(att.attname ORDER BY u.ord)
                     FROM unnest(con.conkey) WITH ORDINALITY AS u(attnum, ord)
                     JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = u.attnum
                   ) AS column_names,
                   (
                     SELECT array_agg(att.attname ORDER BY u.ord)
                     FROM unnest(con.confkey) WITH ORDINALITY AS u(attnum, ord)
                     JOIN pg_attribute att ON att.attrelid = con.confrelid AND att.attnum = u.attnum
                   ) AS foreign_column_names
            FROM pg_constraint con
            JOIN pg_class rel ON rel.oid = con.conrelid
            JOIN pg_namespace nsp ON nsp.oid = con.connamespace
            LEFT JOIN pg_class frel ON frel.oid = con.confrelid
            LEFT JOIN pg_namespace fnsp ON fnsp.oid = frel.relnamespace
            WHERE nsp.nspname = ?
            """;

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.get(tableName);
          if (holder == null) continue;

          String constraintName = rs.getString("constraint_name");
          String conType = rs.getString("constraint_type");

          switch (conType) {
            case "p" -> {
              List<String> cols = parsePgArray(rs.getArray("column_names"));
              holder.primaryKey = new DatabaseSchema.PrimaryKeyModel(constraintName, cols);
            }
            case "u" -> {
              List<String> cols = parsePgArray(rs.getArray("column_names"));
              holder.uniqueKeys.put(
                  constraintName, new DatabaseSchema.UniqueKeyModel(constraintName, cols));
            }
            case "f" -> {
              String fkSchema = getStringOrNull(rs, "foreign_table_schema");
              String fkTable = getStringOrNull(rs, "foreign_table_name");
              List<String> cols = parsePgArray(rs.getArray("column_names"));
              List<String> fcols = parsePgArray(rs.getArray("foreign_column_names"));

              holder.foreignKeys.put(
                  constraintName,
                  new DatabaseSchema.ForeignKeyModel(
                      constraintName,
                      cols,
                      fkSchema,
                      fkTable,
                      fcols,
                      mapPgCascadeRule(rs.getString("delete_rule")),
                      mapPgCascadeRule(rs.getString("update_rule")),
                      ""));
            }
            case "c" -> {
              String def = getStringOrNull(rs, "constraint_def");
              String predicate =
                  (def != null && def.toUpperCase(Locale.ROOT).startsWith("CHECK "))
                      ? def.substring(6)
                      : def;

              holder.checkConstraints.put(
                  constraintName,
                  new DatabaseSchema.CheckConstraintModel(constraintName, predicate));
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
            SELECT i.relname AS index_name,
                   t.relname AS table_name,
                   idx.indisunique AS is_unique,
                   am.amname AS index_type,
                   pg_get_indexdef(idx.indexrelid) AS index_def
            FROM pg_index idx
            JOIN pg_class i ON i.oid = idx.indexrelid
            JOIN pg_class t ON t.oid = idx.indrelid
            JOIN pg_namespace n ON n.oid = t.relnamespace
            JOIN pg_am am ON am.oid = i.relam
            WHERE n.nspname = ? AND NOT idx.indisprimary
            """;

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          TableHolder holder = tables.get(tableName);
          if (holder == null) continue;

          String indexName = rs.getString("index_name");
          boolean isUnique = rs.getBoolean("is_unique");
          String indexType = rs.getString("index_type");
          String indexDef = getStringOrNull(rs, "index_def");

          holder.indices.put(
              indexName,
              new DatabaseSchema.IndexModel(
                  indexName, normalizeType(indexType), isUnique, parseIndexColumns(indexDef), ""));
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

    String sql =
        """
            SELECT v.table_name, v.view_definition, pgd.description
            FROM information_schema.views v
            LEFT JOIN pg_catalog.pg_statio_all_tables st
                   ON st.schemaname = v.table_schema AND st.relname = v.table_name
            LEFT JOIN pg_catalog.pg_description pgd
                   ON pgd.objoid = st.relid AND pgd.objsubid = 0
            WHERE v.table_schema = ?
            """;

    Map<String, DatabaseSchema.ViewModel> views = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String viewName = rs.getString("table_name");
          views.put(
              viewName,
              new DatabaseSchema.ViewModel(
                  viewName,
                  getStringOrNull(rs, "view_definition"),
                  getStringOrNull(rs, "description")));
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

    String sql =
        """
            SELECT p.proname AS routine_name,
                   pg_get_functiondef(p.oid) AS definition,
                   pg_get_function_result(p.oid) AS return_type,
                   pgd.description,
                   p.provolatile AS volatility
            FROM pg_proc p
            JOIN pg_namespace n ON n.oid = p.pronamespace
            LEFT JOIN pg_description pgd ON pgd.objoid = p.oid
            WHERE n.nspname = ? AND p.prokind = ?
            """;

    String prokind = "PROCEDURE".equalsIgnoreCase(routineType) ? "p" : "f";
    Map<String, DatabaseSchema.RoutineModel> routines = new LinkedHashMap<>();

    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      pstmt.setString(2, prokind);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String name = rs.getString("routine_name");
          String rawReturnType = getStringOrNull(rs, "return_type");
          boolean isImmutable = "i".equalsIgnoreCase(rs.getString("volatility"));

          routines.put(
              name,
              new DatabaseSchema.RoutineModel(
                  name,
                  routineType,
                  List.of(),
                  normalizeType(rawReturnType),
                  rawReturnType,
                  isImmutable,
                  getStringOrNull(rs, "definition"),
                  getStringOrNull(rs, "description")));
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

    String sql =
        """
            SELECT trg.tgname AS trigger_name,
                   tbl.relname AS table_name,
                   pg_get_triggerdef(trg.oid) AS definition,
                   trg.tgtype,
                   pgd.description
            FROM pg_trigger trg
            JOIN pg_class tbl ON tbl.oid = trg.tgrelid
            JOIN pg_namespace nsp ON nsp.oid = tbl.relnamespace
            LEFT JOIN pg_description pgd ON pgd.objoid = trg.oid
            WHERE nsp.nspname = ? AND NOT trg.tgisinternal
            """;

    Map<String, DatabaseSchema.TriggerModel> triggers = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String name = rs.getString("trigger_name");
          int tgtype = rs.getInt("tgtype");

          triggers.put(
              name,
              new DatabaseSchema.TriggerModel(
                  name,
                  rs.getString("table_name"),
                  resolveTriggerEvent(tgtype),
                  resolveTriggerTiming(tgtype),
                  getStringOrNull(rs, "definition"),
                  getStringOrNull(rs, "description")));
        }
      }
    }
    return triggers;
  }

  @Override
  protected Map<String, DatabaseSchema.SequenceModel> extractSequences(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String sql =
        """
            SELECT sequence_name, data_type, start_value, minimum_value, maximum_value, increment, cycle_option
            FROM information_schema.sequences
            WHERE sequence_schema = ?
            """;

    Map<String, DatabaseSchema.SequenceModel> sequences = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String seqName = rs.getString("sequence_name");
          String rawDataType = getStringOrNull(rs, "data_type");

          sequences.put(
              seqName,
              new DatabaseSchema.SequenceModel(
                  seqName,
                  normalizeType(rawDataType),
                  getStringOrNull(rs, "start_value"),
                  getStringOrNull(rs, "increment"),
                  getStringOrNull(rs, "minimum_value"),
                  getStringOrNull(rs, "maximum_value"),
                  "YES".equalsIgnoreCase(rs.getString("cycle_option")),
                  ""));
        }
      }
    }
    return sequences;
  }

  @Override
  protected Map<String, DatabaseSchema.CustomTypeModel> extractCustomTypes(
      Connection connection, String catalog, String schema, ExtractionOptions options)
      throws SQLException {

    String sql =
        """
            SELECT t.typname AS type_name,
                   array_agg(e.enumlabel ORDER BY e.enumsortorder) AS enum_values
            FROM pg_type t
            JOIN pg_enum e ON t.oid = e.enumtypid
            JOIN pg_namespace n ON n.oid = t.typnamespace
            WHERE n.nspname = ?
            GROUP BY t.typname
            """;

    Map<String, DatabaseSchema.CustomTypeModel> types = new LinkedHashMap<>();
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, schema);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String typeName = rs.getString("type_name");
          List<String> values = parsePgArray(rs.getArray("enum_values"));

          types.put(typeName, new DatabaseSchema.CustomTypeModel(typeName, "ENUM", values, "", ""));
        }
      }
    }
    return types;
  }

  // =========================================================================
  // HELPER METHODS
  // =========================================================================

  private String resolveTriggerTiming(int tgtype) {
    if ((tgtype & (1 << 1)) != 0) return "BEFORE";
    if ((tgtype & (1 << 6)) != 0) return "INSTEAD OF";
    return "AFTER";
  }

  private String resolveTriggerEvent(int tgtype) {
    boolean isInsert = (tgtype & (1 << 2)) != 0;
    boolean isDelete = (tgtype & (1 << 3)) != 0;
    boolean isUpdate = (tgtype & (1 << 4)) != 0;

    if (isInsert && isUpdate) return "INSERT OR UPDATE";
    if (isInsert) return "INSERT";
    if (isUpdate) return "UPDATE";
    if (isDelete) return "DELETE";

    return "MULTIPLE";
  }

  private String mapPgCascadeRule(String code) {
    if (code == null) return "NO ACTION";
    return switch (code.toLowerCase(Locale.ROOT)) {
      case "c" -> "CASCADE";
      case "n" -> "SET NULL";
      case "d" -> "SET DEFAULT";
      case "r" -> "RESTRICT";
      default -> "NO ACTION";
    };
  }

  private List<String> parsePgArray(java.sql.Array sqlArray) throws SQLException {
    if (sqlArray == null) return List.of();
    Object[] objs = (Object[]) sqlArray.getArray();
    List<String> list = new ArrayList<>();
    for (Object obj : objs) {
      if (obj != null) list.add(obj.toString());
    }
    return list;
  }

  private List<DatabaseSchema.IndexModel.IndexColumn> parseIndexColumns(String indexDef) {
    if (indexDef == null || indexDef.isBlank()) return List.of();
    int openParen = indexDef.lastIndexOf('(');
    int closeParen = indexDef.lastIndexOf(')');
    if (openParen == -1 || closeParen == -1 || openParen >= closeParen) {
      return List.of();
    }

    String colsStr = indexDef.substring(openParen + 1, closeParen);
    String[] colParts = colsStr.split(",");
    List<DatabaseSchema.IndexModel.IndexColumn> result = new ArrayList<>();

    for (String part : colParts) {
      String trimmed = part.trim();
      if (trimmed.isEmpty()) continue;

      String order = "ASC";
      if (trimmed.toUpperCase(Locale.ROOT).endsWith(" DESC")) {
        order = "DESC";
        trimmed = trimmed.substring(0, trimmed.length() - 5).trim();
      } else if (trimmed.toUpperCase(Locale.ROOT).endsWith(" ASC")) {
        trimmed = trimmed.substring(0, trimmed.length() - 4).trim();
      }

      result.add(new DatabaseSchema.IndexModel.IndexColumn(trimmed, order));
    }
    return result;
  }

  private static class TableHolder {
    final String tableName;
    final Map<String, DatabaseSchema.ColumnModel> columns = new LinkedHashMap<>();
    final Map<String, DatabaseSchema.ForeignKeyModel> foreignKeys = new LinkedHashMap<>();
    final Map<String, DatabaseSchema.UniqueKeyModel> uniqueKeys = new LinkedHashMap<>();
    final Map<String, DatabaseSchema.CheckConstraintModel> checkConstraints = new LinkedHashMap<>();
    final Map<String, DatabaseSchema.IndexModel> indices = new LinkedHashMap<>();
    DatabaseSchema.PrimaryKeyModel primaryKey;
    String comment = "";

    TableHolder(String tableName) {
      this.tableName = tableName;
    }
  }
}
