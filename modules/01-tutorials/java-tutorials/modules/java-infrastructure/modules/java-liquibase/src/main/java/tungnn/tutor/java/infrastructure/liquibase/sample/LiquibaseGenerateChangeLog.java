package tungnn.tutor.java.infrastructure.liquibase.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.command.CommandScope;
import liquibase.command.core.GenerateChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;

public class LiquibaseGenerateChangeLog {

  private static final String OUTPUT_FILE = "storage/db/changelog/generated-changelog.xml";
  private static final String DEFAULT_SCHEMA = "HR";

  static void main() {
    String dbUrl = System.getenv("DB_URL");
    String username = System.getenv("DB_USER");
    String password = System.getenv("DB_PASS");

    try {
      try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
        Database database = createDatabase(connection);
        database.setDefaultSchemaName(DEFAULT_SCHEMA);

        generateChangeLog(database, OUTPUT_FILE);
      }
    } catch (Exception e) {
      System.err.println("Failed to generate Liquibase changelog.");
    }
  }

  private static Database createDatabase(Connection connection) throws Exception {
    return DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
  }

  private static void generateChangeLog(Database database, String outputFile) throws Exception {

    CommandScope commandScope = new CommandScope(GenerateChangelogCommandStep.COMMAND_NAME);

    // Pass the database connection to the Liquibase command.
    commandScope.addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database);

    // Specify the output changelog file.
    commandScope.addArgumentValue(GenerateChangelogCommandStep.CHANGELOG_FILE_ARG, outputFile);

    // Execute the Liquibase generate-changelog command.
    commandScope.execute();
  }
}
