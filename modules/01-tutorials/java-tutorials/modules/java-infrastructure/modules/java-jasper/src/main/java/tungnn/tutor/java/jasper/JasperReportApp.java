package tungnn.tutor.java.jasper;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.*;

public class JasperReportApp {

  private static final ClassLoader CLASS_LOADER = JasperReportApp.class.getClassLoader();

  static void main() throws SQLException, JRException {
    // 1. Establish connection with database
    Connection conn = getDBConnection();

    // 2. Load JRXML report template from resources dir
    String templatePath = "jrxml/employee_report.jrxml";
    InputStream templateStream = CLASS_LOADER.getResourceAsStream(templatePath);
    if (templateStream == null) {
      throw new IllegalStateException("Report template not found: " + templatePath);
    }

    // 3. Compile JRXML into JasperReport
    JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

    // 4. Prepare report parameters (if any)
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("reportTitle", "Employee Report");
    parameters.put("createdBy", "JasperReportApp");

    // 5. Fill the report using JDBC connection
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

    // 6. Export the filled report to PDF
    Path pdfPath = Path.of("storage", "employee_report.pdf");
    JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath.toString());
  }

  public static Connection getDBConnection() throws SQLException {
    final String DB_URL = System.getenv("DB_URL");
    final String DB_USER = System.getenv("DB_USER");
    final String DB_PASS = System.getenv("DB_PASS");
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
  }
}
