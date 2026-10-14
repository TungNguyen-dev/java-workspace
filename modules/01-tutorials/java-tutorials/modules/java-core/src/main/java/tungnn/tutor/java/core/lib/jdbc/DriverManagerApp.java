package tungnn.tutor.java.core.lib.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DriverManagerApp {

  private static final String DB_URL = System.getenv("DB_URL");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PASS = System.getenv("DB_PASS");

  public static void main(String[] args) {
    String sql = "SELECT * FROM hr.employees";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
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
}
