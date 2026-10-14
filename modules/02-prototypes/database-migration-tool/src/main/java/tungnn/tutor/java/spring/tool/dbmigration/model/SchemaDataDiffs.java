package tungnn.tutor.java.spring.tool.dbmigration.model;

import java.util.List;
import java.util.Map;

public record SchemaDataDiffs(List<TableDataDiff> tableDataDiffs) {

  public boolean hasChanges() {

    return tableDataDiffs.stream().anyMatch(TableDataDiff::hasChanges);
  }

  /** Lưu vết thay đổi của từng Column Value trong 1 Row */
  public record ColumnDataChange(String columnName, String oldValue, String newValue) {}

  /** Diff cho từng Row theo Identity Key */
  public record RowDiff(
      Map<String, String> identityKey,
      DiffStatus status,
      List<ColumnDataChange> columnChanges,
      Map<String, String> rowData) {}

  /** Diff dữ liệu tổng hợp của 1 Table */
  public record TableDataDiff(
      String tableName,
      List<String> identityKeyColumns,
      long addedCount,
      long modifiedCount,
      long removedCount,
      List<RowDiff> rowDiffs) {

    public boolean hasChanges() {

      return addedCount > 0 || modifiedCount > 0 || removedCount > 0;
    }
  }

  public record TableRowDataDiff(String tableName, RowDiff rowDiff) {}
}
