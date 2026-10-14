package tungnn.tutor.java.core.lib.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class JdbcStatements {

  private static final DataSource dataSource = DataSourceApp.getDataSource();

  public static void statement() {
    String sql = "SELECT * FROM hr.departments";
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        System.out.println(rs.getInt("department_id") + " - " + rs.getString("department_name"));
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error executing Statement", e);
    }
  }

  public static void preparedStatement() {
    String sql = "SELECT * FROM hr.employees WHERE department_id = ?";
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

      stmt.setInt(1, 50);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error executing PreparedStatement", e);
    }
  }

  public static void callableStatement() {
    String sql = "{ call get_employee_by_id(?) }";
    try (Connection conn = dataSource.getConnection();
        CallableStatement stmt = conn.prepareCall(sql)) {

      stmt.setInt(1, 1001);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          System.out.println(rs.getString("first_name"));
        }
      }

    } catch (SQLException e) {
      throw new RuntimeException("Error executing CallableStatement", e);
    }
  }
}
