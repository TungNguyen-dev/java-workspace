package tungnn.tutor.java.core.lib.jdbc.extractor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;

public class Demo {

  public static void main(String[] args) {
    String url = System.getenv("DB_URL");
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASS");

    if (url == null || url.isBlank()) {
      System.err.println("Lỗi: Chưa cấu hình biến môi trường DB_URL");
      System.err.println("Hãy thiết lập: DB_URL, DB_USER, DB_PASS trước khi chạy.");
      return;
    }

    // Đổi catalog/schema phù hợp với Database target
    // PostgreSQL: catalog = null / database_name, schema = "public"
    // Oracle    : catalog = null, schema = "USERNAME" (viết hoa)
    String catalog = null;
    String schema = "HR";

    System.out.println("Connecting to Database...");

    try (Connection connection = DriverManager.getConnection(url, user, password)) {
      System.out.println("Connected successfully!");

      MetadataExtractor extractor = new DefaultMetadataExtractor();

      // -----------------------------------------------------------------
      // DEMO 1: Trích xuất toàn bộ Schema (Mặc định)
      // -----------------------------------------------------------------
      System.out.println("\n>>> Running Full Metadata Extraction...");
      long startTime = System.currentTimeMillis();
      DatabaseSchema dbSchema = extractor.extract(connection, catalog, schema);
      long duration = System.currentTimeMillis() - startTime;

      System.out.printf("Extractor completed in %d ms%n%n", duration);

      // In thông tin đã extract
      printSchemaOverview(dbSchema);
      printDetailedSchema(dbSchema);

    } catch (SQLException e) {
      System.err.println("Database Connection / Extraction Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  // =========================================================================
  // PRINT HELPERS
  // =========================================================================

  private static void printSchemaOverview(DatabaseSchema schema) {
    if (schema == null) {
      System.out.println("Schema is null!");
      return;
    }

    System.out.println("==================================================");
    System.out.println("               SCHEMA OVERVIEW                    ");
    System.out.println("==================================================");
    if (schema.meta() != null) {
      System.out.printf("Database Name  : %s%n", schema.meta().databaseName());
      System.out.printf("Schema Name    : %s%n", schema.meta().schemaName());
      System.out.printf(
          "Engine         : %s (v%s)%n", schema.meta().engineName(), schema.meta().engineVersion());
      System.out.printf("Extracted At   : %s%n", schema.meta().extractedAt());
    }
    System.out.println("--------------------------------------------------");
    System.out.printf(
        "Tables Count    : %d%n", schema.tables() != null ? schema.tables().size() : 0);
    System.out.printf("Views Count     : %d%n", schema.views() != null ? schema.views().size() : 0);
    System.out.printf(
        "Procedures Count: %d%n", schema.procedures() != null ? schema.procedures().size() : 0);
    System.out.printf(
        "Functions Count : %d%n", schema.functions() != null ? schema.functions().size() : 0);
    System.out.printf(
        "Triggers Count  : %d%n", schema.triggers() != null ? schema.triggers().size() : 0);
    System.out.printf(
        "Sequences Count : %d%n", schema.sequences() != null ? schema.sequences().size() : 0);
    System.out.printf(
        "Custom Types    : %d%n", schema.customTypes() != null ? schema.customTypes().size() : 0);
    System.out.println("==================================================\n");
  }

  private static void printDetailedSchema(DatabaseSchema schema) {
    if (schema == null) return;

    // 1. Tables & Columns
    if (schema.tables() != null && !schema.tables().isEmpty()) {
      System.out.println("### 1. TABLES & COLUMNS");
      schema
          .tables()
          .forEach(
              (tableName, table) -> {
                System.out.printf(
                    "%n[Table]: %s (Engine: %s, Comment: %s)%n",
                    tableName,
                    table.engine() != null ? table.engine() : "",
                    formatComment(table.comment()));

                // Gom tất cả item thuộc table vào danh sách để xác định chính xác phần tử cuối cùng
                List<String> items = new ArrayList<>();

                // 1.1 Columns (In trước)
                if (table.columns() != null) {
                  table
                      .columns()
                      .forEach(
                          (colName, col) -> {
                            items.add(
                                String.format(
                                    "Column: %-20s | Raw Type: %-15s | Nullable: %-5b | Default: %-10s | Comment: %s",
                                    col.name(),
                                    col.rawType(),
                                    col.isNullable(),
                                    col.defaultValue(),
                                    formatComment(col.comment())));
                          });
                }

                // 1.2 Primary Key
                if (table.primaryKey() != null) {
                  items.add(
                      String.format(
                          "PK: %s %s", table.primaryKey().name(), table.primaryKey().columns()));
                }

                // 1.3 Foreign Keys
                if (table.foreignKeys() != null && !table.foreignKeys().isEmpty()) {
                  table
                      .foreignKeys()
                      .forEach(
                          (fkName, fk) -> {
                            items.add(
                                String.format(
                                    "FK: %s %s -> %s%s [onDelete: %s]",
                                    fkName,
                                    fk.columns(),
                                    fk.referencedTable(),
                                    fk.referencedColumns(),
                                    fk.onDelete()));
                          });
                }

                // 1.4 Check Constraints
                if (table.checkConstraints() != null && !table.checkConstraints().isEmpty()) {
                  table
                      .checkConstraints()
                      .forEach(
                          (chkName, chk) -> {
                            items.add(
                                String.format(
                                    "CHECK: %s (%s)", chkName, chk.predicateExpression()));
                          });
                }

                // In các items ra console với tiền tố nhánh (├─ hoặc └─)
                for (int i = 0; i < items.size(); i++) {
                  boolean isLast = (i == items.size() - 1);
                  String prefix = isLast ? "  └─ " : "  ├─ ";
                  System.out.println(prefix + items.get(i));
                }
              });
      System.out.println();
    }

    // 2. Views
    if (schema.views() != null && !schema.views().isEmpty()) {
      System.out.println("### 2. VIEWS");
      schema
          .views()
          .forEach(
              (viewName, view) -> {
                System.out.printf(
                    "- [View]: %s (Comment: %s)%n", viewName, formatComment(view.comment()));
              });
      System.out.println();
    }

    // 3. Routines (Functions & Procedures)
    if (schema.functions() != null && !schema.functions().isEmpty()) {
      System.out.println("### 3. FUNCTIONS");
      schema
          .functions()
          .forEach(
              (funcName, func) -> {
                System.out.printf(
                    "- [Function]: %s | ReturnType: %s%n", funcName, func.rawReturnType());
              });
      System.out.println();
    }

    // 4. Triggers
    if (schema.triggers() != null && !schema.triggers().isEmpty()) {
      System.out.println("### 4. TRIGGERS");
      schema
          .triggers()
          .forEach(
              (trigName, trig) -> {
                System.out.printf(
                    "- [Trigger]: %s | Target: %s | Timing: %s | Event: %s%n",
                    trigName, trig.targetTable(), trig.timing(), trig.event());
              });
      System.out.println();
    }

    // 5. Sequences
    if (schema.sequences() != null && !schema.sequences().isEmpty()) {
      System.out.println("### 5. SEQUENCES");
      schema
          .sequences()
          .forEach(
              (seqName, seq) -> {
                System.out.printf(
                    "- [Sequence]: %s | Start: %s | Increment: %s%n",
                    seqName, seq.startValue(), seq.increment());
              });
      System.out.println();
    }

    // 6. Custom Types (ENUMs, Composite, etc.)
    if (schema.customTypes() != null && !schema.customTypes().isEmpty()) {
      System.out.println("### 6. CUSTOM TYPES");
      schema
          .customTypes()
          .forEach(
              (typeName, type) -> {
                System.out.printf(
                    "- [Type]: %s | Category: %s | Values: %s%n",
                    typeName, type.category(), type.enumValues());
              });
      System.out.println();
    }
  }

  /**
   * Chuyển đổi comment về chuỗi an toàn: null-check và replace tất cả ký tự xuống dòng (\r\n, \n,
   * \r) bằng khoảng trắng.
   */
  private static String formatComment(String comment) {
    if (comment == null) {
      return "";
    }
    return comment.replaceAll("\\r?\\n|\\r", " ");
  }
}
