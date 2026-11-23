package tungnn.tutor.java.core.lib.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class JdbcBatchProcessing {

  private final DataSource dataSource;

  public JdbcBatchProcessing(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void insertEmployeesBatch(List<Map<String, Object>> paramsList) {
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
      conn = dataSource.getConnection();
      conn.setAutoCommit(false);

      // Batch processing
      try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
        for (Map<String, Object> params : paramsList) {
          pstmt.clearParameters();
          pstmt.setObject(1, params.get("EMPLOYEE_ID"));
          pstmt.setObject(2, params.get("FIRST_NAME"));
          pstmt.setObject(3, params.get("LAST_NAME"));
          pstmt.setObject(4, params.get("EMAIL"));
          pstmt.setObject(5, params.get("HIRE_DATE"));
          pstmt.setObject(6, params.get("JOB_ID"));
          pstmt.addBatch();
        }

        pstmt.executeBatch();
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
