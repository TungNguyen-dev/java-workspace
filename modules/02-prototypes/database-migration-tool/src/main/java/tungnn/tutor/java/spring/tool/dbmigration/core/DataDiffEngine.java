package tungnn.tutor.java.spring.tool.dbmigration.core;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.spring.tool.dbmigration.model.DatabaseConfig;
import tungnn.tutor.java.spring.tool.dbmigration.model.DiffStatus;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaDataDiffOption;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaDataDiffs;

public class DataDiffEngine {

  public static Stream<SchemaDataDiffs.TableRowDataDiff> streamDataDiff(
      DatabaseConfig refConfig,
      DatabaseConfig targetConfig,
      DatabaseSchema refSchema,
      DatabaseSchema targetSchema,
      SchemaDataDiffOption option) {

    List<String> tablesToDiff = filterTables(refSchema, targetSchema, option);

    return tablesToDiff.stream()
        .flatMap(
            tableName -> {
              DatabaseSchema.TableModel refTable = refSchema.tables().get(tableName);
              DatabaseSchema.TableModel targetTable = targetSchema.tables().get(tableName);

              List<String> identityKeys = getIdentityKeys(refTable);
              if (identityKeys.isEmpty()) {
                // Bảng không có Primary Key -> Bỏ qua hoặc báo lỗi tùy policy
                return Stream.empty();
              }

              return diffTableDataStream(
                  refConfig, targetConfig, tableName, identityKeys, refTable, targetTable);
            });
  }

  private static Stream<SchemaDataDiffs.TableRowDataDiff> diffTableDataStream(
      DatabaseConfig refConfig,
      DatabaseConfig targetConfig,
      String tableName,
      List<String> identityKeys,
      DatabaseSchema.TableModel refTable,
      DatabaseSchema.TableModel targetTable) {

    List<SchemaDataDiffs.TableRowDataDiff> diffList = new ArrayList<>();
    String orderByClause = String.join(", ", identityKeys);
    String sql =
        """
        SELECT * FROM %s.%s ORDER BY %s;
        """;
    String refSql = sql.formatted(refConfig.schemaName(), tableName, orderByClause);
    String targetSql = sql.formatted(targetConfig.schemaName(), tableName, orderByClause);

    try (Connection refConn =
            DriverManager.getConnection(
                refConfig.url(), refConfig.username(), refConfig.password());
        Connection targetConn =
            DriverManager.getConnection(
                targetConfig.url(), targetConfig.username(), targetConfig.password());
        Statement refStmt = refConn.createStatement();
        Statement targetStmt = targetConn.createStatement();
        ResultSet refRs = refStmt.executeQuery(refSql);
        ResultSet targetRs = targetStmt.executeQuery(targetSql)) {

      boolean hasRef = refRs.next();
      boolean hasTarget = targetRs.next();

      while (hasRef || hasTarget) {
        if (hasRef && hasTarget) {
          int cmp = compareKeys(refRs, targetRs, identityKeys);
          if (cmp == 0) {
            // Cùng Key -> So sánh các cột còn lại xem có MODIFIED không
            SchemaDataDiffs.RowDiff diff = compareRows(refRs, targetRs, identityKeys, refTable);
            if (diff != null) {
              diffList.add(new SchemaDataDiffs.TableRowDataDiff(tableName, diff));
            }
            hasRef = refRs.next();
            hasTarget = targetRs.next();
          } else if (cmp < 0) {
            // Ref Key < Target Key -> Row ở Ref đã bị REMOVED bên Target
            diffList.add(
                new SchemaDataDiffs.TableRowDataDiff(
                    tableName, createRowDiff(refRs, identityKeys, DiffStatus.REMOVED)));
            hasRef = refRs.next();
          } else {
            // Ref Key > Target Key -> Row mới được ADDED bên Target
            diffList.add(
                new SchemaDataDiffs.TableRowDataDiff(
                    tableName, createRowDiff(targetRs, identityKeys, DiffStatus.ADDED)));
            hasTarget = targetRs.next();
          }
        } else if (hasRef) {
          diffList.add(
              new SchemaDataDiffs.TableRowDataDiff(
                  tableName, createRowDiff(refRs, identityKeys, DiffStatus.REMOVED)));
          hasRef = refRs.next();
        } else {
          diffList.add(
              new SchemaDataDiffs.TableRowDataDiff(
                  tableName, createRowDiff(targetRs, identityKeys, DiffStatus.ADDED)));
          hasTarget = targetRs.next();
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException(
          "Lỗi dữ liệu khi so sánh bảng " + tableName + ": " + e.getMessage(), e);
    }

    return diffList.stream();
  }

  // =========================================================================
  // Helpers
  // =========================================================================

  private static List<String> filterTables(
      DatabaseSchema ref, DatabaseSchema target, SchemaDataDiffOption option) {
    Set<String> commonTables = new HashSet<>(ref.tables().keySet());
    commonTables.retainAll(target.tables().keySet());

    if (option == null || option.filterMode() == SchemaDataDiffOption.FilterMode.ALL) {
      return new ArrayList<>(commonTables);
    }

    List<String> targetList = option.tableNames() != null ? option.tableNames() : List.of();
    if (option.filterMode() == SchemaDataDiffOption.FilterMode.INCLUDE) {
      commonTables.retainAll(targetList);
    } else if (option.filterMode() == SchemaDataDiffOption.FilterMode.EXCLUDE) {
      targetList.forEach(commonTables::remove);
    }

    return new ArrayList<>(commonTables);
  }

  private static List<String> getIdentityKeys(DatabaseSchema.TableModel table) {
    DatabaseSchema.PrimaryKeyModel pk = table.primaryKey();
    return pk != null ? pk.columns() : List.of();
  }

  private static int compareKeys(ResultSet rs1, ResultSet rs2, List<String> keys)
      throws SQLException {
    for (String key : keys) {
      String val1 = rs1.getString(key);
      String val2 = rs2.getString(key);
      if (val1 == null && val2 == null) continue;
      if (val1 == null) return -1;
      if (val2 == null) return 1;

      int cmp = val1.compareTo(val2);
      if (cmp != 0) return cmp;
    }
    return 0;
  }

  private static SchemaDataDiffs.RowDiff compareRows(
      ResultSet refRs,
      ResultSet targetRs,
      List<String> identityKeys,
      DatabaseSchema.TableModel refTable)
      throws SQLException {
    Map<String, String> keyMap = extractMap(refRs, identityKeys);
    List<SchemaDataDiffs.ColumnDataChange> changes = new ArrayList<>();
    Map<String, String> targetRowData = new HashMap<>();

    ResultSetMetaData meta = refRs.getMetaData();
    int colCount = meta.getColumnCount();

    for (int i = 1; i <= colCount; i++) {
      String colName = meta.getColumnName(i);
      String val1 = refRs.getString(i);
      String val2 = targetRs.getString(colName);

      targetRowData.put(colName, val2);

      if (!Objects.equals(val1, val2)) {
        changes.add(new SchemaDataDiffs.ColumnDataChange(colName, val1, val2));
      }
    }

    if (changes.isEmpty()) {
      return null; // Không có dòng dữ liệu nào thay đổi
    }

    return new SchemaDataDiffs.RowDiff(keyMap, DiffStatus.MODIFIED, changes, targetRowData);
  }

  private static SchemaDataDiffs.RowDiff createRowDiff(
      ResultSet rs, List<String> identityKeys, DiffStatus status) throws SQLException {
    Map<String, String> keyMap = extractMap(rs, identityKeys);
    Map<String, String> rowData = new HashMap<>();

    ResultSetMetaData meta = rs.getMetaData();
    for (int i = 1; i <= meta.getColumnCount(); i++) {
      rowData.put(meta.getColumnName(i), rs.getString(i));
    }

    return new SchemaDataDiffs.RowDiff(keyMap, status, List.of(), rowData);
  }

  private static Map<String, String> extractMap(ResultSet rs, List<String> columns)
      throws SQLException {
    Map<String, String> map = new LinkedHashMap<>();
    for (String col : columns) {
      map.put(col, rs.getString(col));
    }
    return map;
  }
}
