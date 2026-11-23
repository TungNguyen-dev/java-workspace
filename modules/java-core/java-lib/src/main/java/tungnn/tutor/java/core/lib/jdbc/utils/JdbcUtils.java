package tungnn.tutor.java.core.lib.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JdbcUtils {

  private JdbcUtils() {}

  public static Connection getConnection(String url, String username, String password) {
    try {
      return DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage(), e);
    }
  }

  /**
   * Chú ý: Cần giữ Statement mở cùng với ResultSet. Người gọi hàm chịu trách nhiệm đóng ResultSet
   * hoặc Connection.
   */
  public static ResultSet executeQuery(Connection conn, String sql) {
    try {
      Statement stmt = conn.createStatement();
      return stmt.executeQuery(sql);
    } catch (SQLException e) {
      throw new RuntimeException("Lỗi thực thi truy vấn Query: " + e.getMessage(), e);
    }
  }

  public static int executeDMLQuery(Connection conn, String sql, Parameters parameters) {
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameters(stmt, parameters);
      return stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Lỗi thực thi DML Query: " + e.getMessage(), e);
    }
  }

  /**
   * Thực thi thao tác Bulk / Batch Insert, Update, Delete
   *
   * @return Mảng chứa số lượng bản ghi bị ảnh hưởng bởi mỗi câu lệnh
   */
  public static int[] executeBulkDMLQuery(
      Connection conn, String sql, List<Parameters> batchParameters) {

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
      if (batchParameters != null) {
        for (Parameters parameters : batchParameters) {
          setParameters(stmt, parameters);
          stmt.addBatch(); // Thêm vào lô (batch)
        }
      }
      return stmt.executeBatch(); // Thực thi toàn bộ lô
    } catch (SQLException e) {
      throw new RuntimeException("Lỗi thực thi Bulk DML Query: " + e.getMessage(), e);
    }
  }

  /** Helper method để bind tham số vào PreparedStatement theo đúng vị trí */
  private static void setParameters(PreparedStatement stmt, Parameters parameters)
      throws SQLException {
    if (parameters == null || parameters.parameters() == null) {
      return;
    }

    // Tạo bản sao ArrayList để sắp xếp, tránh lỗi UnsupportedOperationException nếu list là
    // Immutable
    List<Parameters.Parameter> params = new ArrayList<>(parameters.parameters());
    params.sort(Comparator.comparing(Parameters.Parameter::pos));

    for (var param : params) {
      if (param.type() != null) {
        stmt.setObject(param.pos(), param.value(), param.type());
      } else {
        stmt.setObject(param.pos(), param.value());
      }
    }
  }
}
