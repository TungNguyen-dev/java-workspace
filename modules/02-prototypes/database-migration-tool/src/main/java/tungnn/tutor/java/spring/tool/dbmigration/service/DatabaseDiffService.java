package tungnn.tutor.java.spring.tool.dbmigration.service;

import java.util.stream.Stream;
import tungnn.tutor.java.spring.tool.dbmigration.model.DatabaseConfig;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaDataDiffOption;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaDataDiffs;
import tungnn.tutor.java.spring.tool.dbmigration.model.SchemaStructureDiffs;

public interface DatabaseDiffService {

  SchemaStructureDiffs diffSchemaStructure(DatabaseConfig reference, DatabaseConfig target);

  SchemaDataDiffs diffSchemaData(
      DatabaseConfig reference, DatabaseConfig target, SchemaDataDiffOption option);

  Stream<SchemaDataDiffs.TableRowDataDiff> streamSchemaDataDiff(
      DatabaseConfig reference, DatabaseConfig target, SchemaDataDiffOption option);
}
