package tungnn.tutor.java.spring.tool.dbmigration.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tungnn.tutor.java.core.lib.jdbc.extractor.MetadataExtractor;
import tungnn.tutor.java.core.lib.jdbc.extractor.model.DatabaseSchema;
import tungnn.tutor.java.spring.tool.dbmigration.core.DataDiffEngine;
import tungnn.tutor.java.spring.tool.dbmigration.core.StructureDiffAnalyzer;
import tungnn.tutor.java.spring.tool.dbmigration.model.*;
import tungnn.tutor.java.spring.tool.dbmigration.service.DatabaseDiffService;

public class DatabaseDiffServiceImpl implements DatabaseDiffService {

  private final MetadataExtractor metadataExtractor;

  public DatabaseDiffServiceImpl(MetadataExtractor metadataExtractor) {
    this.metadataExtractor = metadataExtractor;
  }

  @Override
  public SchemaStructureDiffs diffSchemaStructure(DatabaseConfig reference, DatabaseConfig target) {
    try (Connection refConn = getConnection(reference);
        Connection targetConn = getConnection(target)) {

      DatabaseSchema refSchema = metadataExtractor.extract(refConn, null, reference.schemaName());
      DatabaseSchema targetSchema =
          metadataExtractor.extract(targetConn, null, target.schemaName());

      return StructureDiffAnalyzer.compare(refSchema, targetSchema);
    } catch (SQLException e) {
      throw new RuntimeException("Lỗi khi so sánh cấu trúc CSDL: " + e.getMessage(), e);
    }
  }

  @Override
  public Stream<SchemaDataDiffs.TableRowDataDiff> streamSchemaDataDiff(
      DatabaseConfig reference, DatabaseConfig target, SchemaDataDiffOption option) {

    try (Connection refConn = getConnection(reference);
        Connection targetConn = getConnection(target)) {

      DatabaseSchema refSchema = metadataExtractor.extract(refConn, null, reference.schemaName());
      DatabaseSchema targetSchema =
          metadataExtractor.extract(targetConn, null, target.schemaName());

      return DataDiffEngine.streamDataDiff(reference, target, refSchema, targetSchema, option);
    } catch (SQLException e) {
      throw new RuntimeException(
          "Lỗi khi trích xuất metadata để diff dữ liệu: " + e.getMessage(), e);
    }
  }

  @Override
  public SchemaDataDiffs diffSchemaData(
      DatabaseConfig reference, DatabaseConfig target, SchemaDataDiffOption option) {

    try (Stream<SchemaDataDiffs.TableRowDataDiff> stream =
        streamSchemaDataDiff(reference, target, option)) {
      Map<String, List<SchemaDataDiffs.TableRowDataDiff>> groupedByTable =
          stream.collect(Collectors.groupingBy(SchemaDataDiffs.TableRowDataDiff::tableName));

      List<SchemaDataDiffs.TableDataDiff> tableDataDiffs =
          groupedByTable.entrySet().stream()
              .map(
                  entry -> {
                    String tableName = entry.getKey();
                    List<SchemaDataDiffs.TableRowDataDiff> rowDiffs = entry.getValue();

                    long added =
                        rowDiffs.stream()
                            .filter(r -> r.rowDiff().status() == DiffStatus.ADDED)
                            .count();
                    long modified =
                        rowDiffs.stream()
                            .filter(r -> r.rowDiff().status() == DiffStatus.MODIFIED)
                            .count();
                    long removed =
                        rowDiffs.stream()
                            .filter(r -> r.rowDiff().status() == DiffStatus.REMOVED)
                            .count();

                    // Lấy identity keys từ rowDiff đầu tiên (nếu có)
                    List<String> identityKeys =
                        rowDiffs.isEmpty()
                            ? List.of()
                            : List.copyOf(rowDiffs.get(0).rowDiff().identityKey().keySet());

                    List<SchemaDataDiffs.RowDiff> listRowDiffs =
                        rowDiffs.stream()
                            .map(SchemaDataDiffs.TableRowDataDiff::rowDiff)
                            .collect(Collectors.toList());

                    return new SchemaDataDiffs.TableDataDiff(
                        tableName, identityKeys, added, modified, removed, listRowDiffs);
                  })
              .collect(Collectors.toList());

      return new SchemaDataDiffs(tableDataDiffs);
    }
  }

  private Connection getConnection(DatabaseConfig config) throws SQLException {
    return DriverManager.getConnection(config.url(), config.username(), config.password());
  }
}
