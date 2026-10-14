package tungnn.tutor.java.spring.tool.dbmigration.model;

import java.util.List;

public record SchemaStructureDiffs(
    List<TableStructureDiff> tableDiffs,
    List<ObjectStructureDiff> viewDiffs,
    List<ObjectStructureDiff> procedureDiffs,
    List<ObjectStructureDiff> functionDiffs,
    List<ObjectStructureDiff> triggerDiffs,
    List<ObjectStructureDiff> customTypeDiffs,
    List<ObjectStructureDiff> sequenceDiffs) {

  public boolean hasChanges() {

    return !tableDiffs.isEmpty()
        || !viewDiffs.isEmpty()
        || !procedureDiffs.isEmpty()
        || !functionDiffs.isEmpty()
        || !triggerDiffs.isEmpty()
        || !customTypeDiffs.isEmpty()
        || !sequenceDiffs.isEmpty();
  }

  /** Đại diện cho sự thay đổi chi tiết của 1 attribute hoặc 1 comment */
  public record AttributeChange(String attributeName, String oldValue, String newValue) {}

  /** Diff cho từng column trong table */
  public record ColumnDiff(String columnName, DiffStatus status, List<AttributeChange> changes) {}

  /** Diff cho constraint (PK, FK, Unique, Check) */
  public record ConstraintDiff(
      String constraintName,
      ConstraintType constraintType, // Đã đổi từ String sang ConstraintType
      DiffStatus status,
      List<AttributeChange> changes) {}

  /** Diff cho index */
  public record IndexDiff(String indexName, DiffStatus status, List<AttributeChange> changes) {}

  /** Diff tổng hợp cho một Table (bao gồm columns, constraints, indexes và comment) */
  public record TableStructureDiff(
      String tableName,
      DiffStatus status,
      List<AttributeChange> tableChanges, // Thay đổi metadata bảng (Comment, Engine, Collation...)
      List<ColumnDiff> columnDiffs,
      List<ConstraintDiff> constraintDiffs,
      List<IndexDiff> indexDiffs) {}

  /**
   * Generic Diff cho Programmable Objects (Views, Procedures, Functions, Triggers) và
   *
   * <p>Types/Sequences
   */
  public record ObjectStructureDiff(
      String objectName,
      ObjectType objectType, // Đã đổi từ String sang ObjectType
      DiffStatus status,
      List<AttributeChange> changes) {}
}
