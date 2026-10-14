package tungnn.tutor.java.document.core;

import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public final class DocumentTextExtractorUtil {

  private DocumentTextExtractorUtil() {}

  public static String extractText(XWPFDocument document) {
    var extractor = new XWPFWordExtractor(document);
    return extractor.getText().strip();
  }

  public static String extractText(Workbook document) {
    if (!(document instanceof XSSFWorkbook xssfWorkbook)) {
      throw new UnsupportedOperationException("Only XSSFWorkbook (.xlsx) is supported.");
    }
    var extractor = new XSSFExcelExtractor(xssfWorkbook);
    return extractor.getText().strip();
  }

  public static String extractText(XMLSlideShow document) {
    var extractor = new SlideShowExtractor<>(document);
    return extractor.getText().strip();
  }
}
