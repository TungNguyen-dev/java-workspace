package tungnn.tutor.java.core.lib.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

public class JdbcDMLStatements {

  private final DataSource dataSource;

  public JdbcDMLStatements(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public int insertEmployee(Map<String, Object> params) throws SQLException {
    String sql =
        """
        INSERT INTO EMPLOYEES (
            EMPLOYEE_ID,
            FIRST_NAME,
            LAST_NAME,
            EMAIL,
            HIRE_DATE,
            JOB_ID
        ) VALUES (?, ?, ?, ?, ?, ?)
        """;

    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setObject(1, params.get("EMPLOYEE_ID"));
      pstmt.setObject(2, params.get("FIRST_NAME"));
      pstmt.setObject(3, params.get("LAST_NAME"));
      pstmt.setObject(4, params.get("EMAIL"));
      pstmt.setObject(5, params.get("HIRE_DATE"));
      pstmt.setObject(6, params.get("JOB_ID"));

      return pstmt.executeUpdate();
    }
  }

  public int updateEmployeeEmail(Map<String, Object> params) throws SQLException {
    String sql =
        """
        UPDATE EMPLOYEES
        SET EMAIL = ?
        WHERE EMPLOYEE_ID = ?
        """;

    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setObject(1, params.get("EMAIL"));
      pstmt.setObject(2, params.get("EMPLOYEE_ID"));

      return pstmt.executeUpdate();
    }
  }

  public int deleteEmployee(Map<String, Object> params) throws SQLException {
    String sql =
        """
        DELETE FROM EMPLOYEES
        WHERE EMPLOYEE_ID = ?
        """;

    try (Connection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setObject(1, params.get("EMPLOYEE_ID"));

      return pstmt.executeUpdate();
    }
  }
}
