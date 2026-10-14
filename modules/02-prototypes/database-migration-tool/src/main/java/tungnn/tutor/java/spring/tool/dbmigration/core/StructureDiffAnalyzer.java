package tungnn.tutor.java.spring.tool.dbmigration.core;

import java.util.*;
import java.util.function.Function;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema.ColumnModel;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema.IndexModel;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema.TableModel;
import tungnn.tutor.java.spring.tool.dbmigration.model.ConstraintType;
import tungnn.tutor.java.spring.tool.dbmigration.model.DiffStatus;
import tungnn.tutor.java.spring.tool.dbmigration.model.ObjectType;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaStructureDiffs;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaStructureDiffs.*;

public class StructureDiffAnalyzer {

  public static SchemaStructureDiffs compare(DatabaseSchema ref, DatabaseSchema target) {
    List<SchemaStructureDiffs.TableStructureDiff> tableDiffs =
        compareTables(ref.tables(), target.tables());
    List<SchemaStructureDiffs.ObjectStructureDiff> viewDiffs =
        compareObjects(
            ref.views(),
            target.views(),
            ObjectType.VIEW,
            DatabaseSchema.ViewModel::queryDefinition);
    List<SchemaStructureDiffs.ObjectStructureDiff> procDiffs =
        compareObjects(
            ref.procedures(),
            target.procedures(),
            ObjectType.PROCEDURE,
            DatabaseSchema.RoutineModel::routineBody);
    List<SchemaStructureDiffs.ObjectStructureDiff> funcDiffs =
        compareObjects(
            ref.functions(),
            target.functions(),
            ObjectType.FUNCTION,
            DatabaseSchema.RoutineModel::routineBody);
    List<SchemaStructureDiffs.ObjectStructureDiff> trigDiffs =
        compareObjects(
            ref.triggers(),
            target.triggers(),
            ObjectType.TRIGGER,
            DatabaseSchema.TriggerModel::definition);
    List<SchemaStructureDiffs.ObjectStructureDiff> seqDiffs =
        compareObjects(
            ref.sequences(),
            target.sequences(),
            ObjectType.SEQUENCE,
            DatabaseSchema.SequenceModel::startValue);
    List<SchemaStructureDiffs.ObjectStructureDiff> typeDiffs =
        compareObjects(
            ref.customTypes(),
            target.customTypes(),
            ObjectType.CUSTOM_TYPE,
            DatabaseSchema.CustomTypeModel::category);

    return new SchemaStructureDiffs(
        tableDiffs, viewDiffs, procDiffs, funcDiffs, trigDiffs, typeDiffs, seqDiffs);
  }

  // =========================================================================
  // Table Diffing Logic
  // =========================================================================

  private static List<SchemaStructureDiffs.TableStructureDiff> compareTables(
      Map<String, DatabaseSchema.TableModel> refTables,
      Map<String, DatabaseSchema.TableModel> targetTables) {

    List<SchemaStructureDiffs.TableStructureDiff> result = new ArrayList<>();
    Set<String> allTableNames = new TreeSet<>(refTables.keySet());
    allTableNames.addAll(targetTables.keySet());

    for (String tableName : allTableNames) {
      DatabaseSchema.TableModel refTable = refTables.get(tableName);
      DatabaseSchema.TableModel targetTable = targetTables.get(tableName);

      if (refTable == null) {
        // Table chỉ có ở Target -> ADDED
        result.add(
            new TableStructureDiff(
                tableName, DiffStatus.ADDED, List.of(), List.of(), List.of(), List.of()));
      } else if (targetTable == null) {
        // Table chỉ có ở Reference -> REMOVED
        result.add(
            new TableStructureDiff(
                tableName, DiffStatus.REMOVED, List.of(), List.of(), List.of(), List.of()));
      } else {
        // Cả 2 đều có -> So sánh chi tiết
        List<AttributeChange> tableChanges = compareTableMetadata(refTable, targetTable);
        List<ColumnDiff> colDiffs = compareColumns(refTable.columns(), targetTable.columns());
        List<ConstraintDiff> constraintDiffs = compareConstraints(refTable, targetTable);
        List<IndexDiff> indexDiffs = compareIndexes(refTable.indexes(), targetTable.indexes());

        boolean isModified =
            !tableChanges.isEmpty()
                || !colDiffs.isEmpty()
                || !constraintDiffs.isEmpty()
                || !indexDiffs.isEmpty();

        DiffStatus status = isModified ? DiffStatus.MODIFIED : DiffStatus.UNCHANGED;
        result.add(
            new TableStructureDiff(
                tableName, status, tableChanges, colDiffs, constraintDiffs, indexDiffs));
      }
    }
    return result;
  }

  private static List<AttributeChange> compareTableMetadata(TableModel ref, TableModel target) {
    List<AttributeChange> changes = new ArrayList<>();
    checkChange(changes, "engine", ref.engine(), target.engine());
    checkChange(changes, "collation", ref.collation(), target.collation());
    checkChange(changes, "charset", ref.charset(), target.charset());
    checkChange(changes, "comment", ref.comment(), target.comment());
    return changes;
  }

  private static List<ColumnDiff> compareColumns(
      Map<String, ColumnModel> refCols, Map<String, ColumnModel> targetCols) {

    List<ColumnDiff> diffs = new ArrayList<>();
    Set<String> allCols = new TreeSet<>(refCols.keySet());
    allCols.addAll(targetCols.keySet());

    for (String colName : allCols) {
      ColumnModel refCol = refCols.get(colName);
      ColumnModel targetCol = targetCols.get(colName);

      if (refCol == null) {
        diffs.add(new ColumnDiff(colName, DiffStatus.ADDED, List.of()));
      } else if (targetCol == null) {
        diffs.add(new ColumnDiff(colName, DiffStatus.REMOVED, List.of()));
      } else {
        List<AttributeChange> changes = new ArrayList<>();
        checkChange(changes, "dataType", refCol.dataType(), targetCol.dataType());
        checkChange(changes, "length", refCol.length(), targetCol.length());
        checkChange(
            changes,
            "isNullable",
            String.valueOf(refCol.isNullable()),
            String.valueOf(targetCol.isNullable()));
        checkChange(changes, "defaultValue", refCol.defaultValue(), targetCol.defaultValue());
        checkChange(
            changes,
            "isAutoIncrement",
            String.valueOf(refCol.isAutoIncrement()),
            String.valueOf(targetCol.isAutoIncrement()));
        checkChange(changes, "comment", refCol.comment(), targetCol.comment());

        DiffStatus status = changes.isEmpty() ? DiffStatus.UNCHANGED : DiffStatus.MODIFIED;
        diffs.add(new ColumnDiff(colName, status, changes));
      }
    }
    return diffs;
  }

  private static List<ConstraintDiff> compareConstraints(TableModel ref, TableModel target) {
    List<ConstraintDiff> diffs = new ArrayList<>();

    // 1. Primary Key
    if (ref.primaryKey() != null || target.primaryKey() != null) {
      if (ref.primaryKey() == null) {
        diffs.add(
            new ConstraintDiff(
                target.primaryKey().name(),
                ConstraintType.PRIMARY_KEY,
                DiffStatus.ADDED,
                List.of()));
      } else if (target.primaryKey() == null) {
        diffs.add(
            new ConstraintDiff(
                ref.primaryKey().name(),
                ConstraintType.PRIMARY_KEY,
                DiffStatus.REMOVED,
                List.of()));
      } else {
        List<AttributeChange> changes = new ArrayList<>();
        checkChange(
            changes,
            "columns",
            ref.primaryKey().columns().toString(),
            target.primaryKey().columns().toString());
        DiffStatus status = changes.isEmpty() ? DiffStatus.UNCHANGED : DiffStatus.MODIFIED;
        diffs.add(
            new ConstraintDiff(
                ref.primaryKey().name(), ConstraintType.PRIMARY_KEY, status, changes));
      }
    }

    // 2. Foreign Keys
    compareNamedMap(
        ref.foreignKeys(),
        target.foreignKeys(),
        ConstraintType.FOREIGN_KEY,
        diffs,
        (r, t, c) -> {
          checkChange(c, "referencedTable", r.referencedTable(), t.referencedTable());
          checkChange(c, "columns", r.columns().toString(), t.columns().toString());
        });

    // 3. Unique Keys
    compareNamedMap(
        ref.uniqueKeys(),
        target.uniqueKeys(),
        ConstraintType.UNIQUE,
        diffs,
        (r, t, c) -> {
          checkChange(c, "columns", r.columns().toString(), t.columns().toString());
        });

    // 4. Check Constraints
    compareNamedMap(
        ref.checkConstraints(),
        target.checkConstraints(),
        ConstraintType.CHECK,
        diffs,
        (r, t, c) -> {
          checkChange(c, "predicateExpression", r.predicateExpression(), t.predicateExpression());
        });

    return diffs;
  }

  private static List<IndexDiff> compareIndexes(
      Map<String, IndexModel> refIndexes, Map<String, IndexModel> targetIndexes) {
    List<IndexDiff> diffs = new ArrayList<>();
    Set<String> allIndexes = new TreeSet<>(refIndexes.keySet());
    allIndexes.addAll(targetIndexes.keySet());

    for (String idxName : allIndexes) {
      IndexModel refIdx = refIndexes.get(idxName);
      IndexModel targetIdx = targetIndexes.get(idxName);

      if (refIdx == null) {
        diffs.add(new IndexDiff(idxName, DiffStatus.ADDED, List.of()));
      } else if (targetIdx == null) {
        diffs.add(new IndexDiff(idxName, DiffStatus.REMOVED, List.of()));
      } else {
        List<AttributeChange> changes = new ArrayList<>();
        checkChange(changes, "type", refIdx.type(), targetIdx.type());
        checkChange(
            changes,
            "isUnique",
            String.valueOf(refIdx.isUnique()),
            String.valueOf(targetIdx.isUnique()));
        DiffStatus status = changes.isEmpty() ? DiffStatus.UNCHANGED : DiffStatus.MODIFIED;
        diffs.add(new IndexDiff(idxName, status, changes));
      }
    }
    return diffs;
  }

  // =========================================================================
  // Programmable Object Diffing Logic
  // =========================================================================

  private static <T> List<ObjectStructureDiff> compareObjects(
      Map<String, T> refMap,
      Map<String, T> targetMap,
      ObjectType objectType,
      Function<T, String> bodyExtractor) {

    List<ObjectStructureDiff> diffs = new ArrayList<>();
    Set<String> allNames = new TreeSet<>(refMap.keySet());
    allNames.addAll(targetMap.keySet());

    for (String name : allNames) {
      T refObj = refMap.get(name);
      T targetObj = targetMap.get(name);

      if (refObj == null) {
        diffs.add(new ObjectStructureDiff(name, objectType, DiffStatus.ADDED, List.of()));
      } else if (targetObj == null) {
        diffs.add(new ObjectStructureDiff(name, objectType, DiffStatus.REMOVED, List.of()));
      } else {
        List<AttributeChange> changes = new ArrayList<>();
        checkChange(
            changes, "definition", bodyExtractor.apply(refObj), bodyExtractor.apply(targetObj));
        DiffStatus status = changes.isEmpty() ? DiffStatus.UNCHANGED : DiffStatus.MODIFIED;
        diffs.add(new ObjectStructureDiff(name, objectType, status, changes));
      }
    }
    return diffs;
  }

  // Helper cho việc kiểm tra giá trị thay đổi
  private static void checkChange(
      List<AttributeChange> changes, String attrName, String oldVal, String newVal) {
    if (!Objects.equals(oldVal, newVal)) {
      changes.add(new AttributeChange(attrName, oldVal, newVal));
    }
  }

  private static <T> void compareNamedMap(
      Map<String, T> refMap,
      Map<String, T> targetMap,
      ConstraintType type,
      List<ConstraintDiff> diffs,
      BiConsumerWithChanges<T> comparator) {

    Set<String> names = new TreeSet<>(refMap.keySet());
    names.addAll(targetMap.keySet());

    for (String name : names) {
      T ref = refMap.get(name);
      T target = targetMap.get(name);

      if (ref == null) {
        diffs.add(new ConstraintDiff(name, type, DiffStatus.ADDED, List.of()));
      } else if (target == null) {
        diffs.add(new ConstraintDiff(name, type, DiffStatus.REMOVED, List.of()));
      } else {
        List<AttributeChange> changes = new ArrayList<>();
        comparator.accept(ref, target, changes);
        DiffStatus status = changes.isEmpty() ? DiffStatus.UNCHANGED : DiffStatus.MODIFIED;
        diffs.add(new ConstraintDiff(name, type, status, changes));
      }
    }
  }

  @FunctionalInterface
  private interface BiConsumerWithChanges<T> {
    void accept(T ref, T target, List<AttributeChange> changes);
  }
}
