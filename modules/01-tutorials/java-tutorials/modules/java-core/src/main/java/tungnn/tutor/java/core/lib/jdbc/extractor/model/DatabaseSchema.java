package tungnn.tutor.java.core.lib.jdbc.extractor.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Root Schema Model chứa toàn bộ metadata đã được normalize. */
public record DatabaseSchema(
    SchemaMeta meta,
    Map<String, TableModel> tables,
    Map<String, ViewModel> views,
    Map<String, RoutineModel> procedures,
    Map<String, RoutineModel> functions,
    Map<String, TriggerModel> triggers,
    Map<String, SequenceModel> sequences,
    Map<String, CustomTypeModel> customTypes) {

  // =========================================================================
  // 1. Metadata
  // =========================================================================

  public record SchemaMeta(
      String databaseName,
      String schemaName,
      String engineName,
      String engineVersion,
      Instant extractedAt) {}

  // =========================================================================
  // 2. Table & Related Sub-models
  // =========================================================================

  public record TableModel(
      String name,
      String engine,
      String collation,
      String charset,
      String comment,
      Map<String, ColumnModel> columns,
      PrimaryKeyModel primaryKey,
      Map<String, ForeignKeyModel> foreignKeys,
      Map<String, UniqueKeyModel> uniqueKeys,
      Map<String, CheckConstraintModel> checkConstraints,
      Map<String, IndexModel> indexes) {}

  public record ColumnModel(
      String name,
      String dataType,
      String rawType,
      String length,
      String precision,
      String scale,
      boolean isNullable,
      String defaultValue,
      boolean isAutoIncrement,
      String ordinalPosition,
      String comment) {}

  // =========================================================================
  // 3. Constraints & Indexes
  // =========================================================================

  public record PrimaryKeyModel(String name, List<String> columns) {}

  public record UniqueKeyModel(String name, List<String> columns) {}

  public record CheckConstraintModel(String name, String predicateExpression) {}

  public record ForeignKeyModel(
      String name,
      List<String> columns,
      String referencedSchema,
      String referencedTable,
      List<String> referencedColumns,
      String onDelete,
      String onUpdate,
      String comment) {}

  public record IndexModel(
      String name, String type, boolean isUnique, List<IndexColumn> columns, String comment) {

    public record IndexColumn(String name, String order) {}
  }

  // =========================================================================
  // 4. Views & Programmable Objects
  // =========================================================================

  public record ViewModel(String name, String queryDefinition, String comment) {}

  public record RoutineModel(
      String name,
      String type,
      List<ParameterModel> parameters,
      String returnType,
      String rawReturnType,
      boolean isDeterministic,
      String routineBody,
      String comment) {

    public record ParameterModel(
        String name, String mode, String dataType, String rawType, String ordinalPosition) {}
  }

  public record TriggerModel(
      String name,
      String targetTable,
      String event,
      String timing,
      String definition,
      String comment) {}

  // =========================================================================
  // 5. Types & Sequences
  // =========================================================================

  public record SequenceModel(
      String name,
      String dataType,
      String startValue,
      String increment,
      String minValue,
      String maxValue,
      boolean isCycle,
      String comment) {}

  public record CustomTypeModel(
      String name, String category, List<String> enumValues, String baseType, String comment) {}
}
