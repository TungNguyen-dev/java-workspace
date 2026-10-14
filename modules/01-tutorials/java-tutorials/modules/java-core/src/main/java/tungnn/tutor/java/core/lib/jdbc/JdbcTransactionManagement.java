package tungnn.tutor.java.core.lib.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.util.Map;

public class JdbcTransactionManagement {

  private static final String DB_URL = System.getenv("DB_URL");
  private static final String DB_USER = System.getenv("DB_USER");
  private static final String DB_PASS = System.getenv("DB_PASS");

  public void insertEmployeeWithTransaction(Map<String, Object> params) {
    final String SQL =
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
    Connection conn = null;

    try {
      conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
      conn.setAutoCommit(false);

      try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
        pstmt.clearParameters();
        pstmt.setObject(1, params.get("EMPLOYEE_ID"));
        pstmt.setObject(2, params.get("FIRST_NAME"));
        pstmt.setObject(3, params.get("LAST_NAME"));
        pstmt.setObject(4, params.get("EMAIL"));
        pstmt.setObject(5, params.get("HIRE_DATE"));
        pstmt.setObject(6, params.get("JOB_ID"));
        pstmt.executeUpdate();

        Savepoint savepoint = conn.setSavepoint("AfterFirstInsert");

        pstmt.clearParameters();
        pstmt.setObject(1, params.get("EMPLOYEE_ID"));
        pstmt.setObject(2, params.get("FIRST_NAME"));
        pstmt.setObject(3, params.get("LAST_NAME"));
        pstmt.setObject(4, params.get("EMAIL"));
        pstmt.setObject(5, params.get("HIRE_DATE"));
        pstmt.setObject(6, params.get("JOB_ID"));
        pstmt.executeUpdate();

        conn.rollback(savepoint);
      }

      conn.commit();
    } catch (Exception e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (Exception rollbackEx) {
          rollbackEx.printStackTrace();
        }
      }
    }
  }
}
