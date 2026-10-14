package tungnn.tutor.java.spring.tool.dbmigration.model;

import java.util.List;

public record SchemaDataDiffOption(FilterMode filterMode, List<String> tableNames) {

  /** Tùy chọn mặc định: So sánh dữ liệu của tất cả các bảng. */
  public static SchemaDataDiffOption defaultOption() {
    return new SchemaDataDiffOption(FilterMode.ALL, List.of());
  }

  public enum FilterMode {
    ALL,
    INCLUDE,
    EXCLUDE
  }
}
