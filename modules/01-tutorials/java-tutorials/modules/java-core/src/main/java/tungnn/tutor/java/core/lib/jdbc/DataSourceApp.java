package tungnn.tutor.java.core.lib.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import oracle.jdbc.pool.OracleDataSource;

public class DataSourceApp {

  private static final String DB_URL = System.getenv("DB_URL");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PASS = System.getenv("DB_PASS");

  private static final DataSource dataSource = getDataSource();

  public static void main(String[] args) {
    String sql = "SELECT * FROM hr.employees";

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      System.out.println("Connected to database");
      while (rs.next()) {
        Integer employeeId = rs.getInt("employee_id");
        String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
        System.out.println(employeeId + " - " + fullName);
      }

    } catch (Exception e) {
      System.out.println("Cannot connect to database: " + e.getMessage());
    }
  }

  public static DataSource getDataSource() {
    try {
      OracleDataSource dataSource = new OracleDataSource();
      dataSource.setURL(DB_URL);
      dataSource.setUser(DB_USER);
      dataSource.setPassword(DB_PASS);
      return dataSource;
    } catch (Exception e) {
      System.out.println("Cannot create data source: " + e.getMessage());
      return null;
    }
  }
}
