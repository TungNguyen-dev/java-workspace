package tungnn.tutor.java.core.lib.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class DataSourceApp {

  private static final String DB_URL = System.getenv("DB_URL");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PASS = System.getenv("DB_PASS");

  private static final DataSource dataSource = getDataSource();

  static void main(String[] args) {
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
    if (DB_URL == null) {
      System.out.println("Cannot create data source: DB_URL is not set");
      return null;
    }
    return new DriverManagerDataSource(DB_URL, DB_USER, DB_PASS);
  }

  private record DriverManagerDataSource(String url, String user, String password)
      implements DataSource {

    @Override
    public Connection getConnection() throws SQLException {
      return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection(String username, String pass) throws SQLException {
      return DriverManager.getConnection(url, username, pass);
    }

    @Override
    public PrintWriter getLogWriter() {
      return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {}

    @Override
    public int getLoginTimeout() {
      return DriverManager.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) {
      DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new SQLException("Not a wrapper for " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
      return false;
    }
  }
}
