package tungnn.tutor.java.document.adapter.markdown;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class MarkdownWriterUtils {

  private MarkdownWriterUtils() {}

  /**
   * Chuyển đổi chuỗi HTML sang Markdown. - Loại bỏ thẻ anchor name <a name="..."></a> và chuỗi
   * attribute {#...} - Unwrap các backlink nội bộ (dạng .html hoặc relative link), chỉ giữ lại phần
   * text bên trong.
   *
   * @param htmlString Chuỗi HTML đầu vào
   * @return Chuỗi Markdown đã được làm sạch
   */
  public static String convertHtmlToMarkdown(String htmlString) {
    if (htmlString == null || htmlString.isBlank()) {
      return "";
    }

    // BƯỚC 1: Tiền xử lý HTML bằng Jsoup để làm sạch các thẻ link không mong muốn
    Document doc = Jsoup.parseBodyFragment(htmlString);

    // 1.1. Xóa thẻ <a name="..."></a> rỗng (loại bỏ nguồn sinh {#jls-2.2-100})
    for (Element a : doc.select("a[name]")) {
      if (a.text().isBlank() && a.children().isEmpty()) {
        a.remove();
      } else {
        a.removeAttr("name");
      }
    }

    // 1.2. Unwrap các link nội bộ (.html hoặc không chứa protocol http/https)
    // Giữ lại text bên trong, xóa bỏ định dạng hyperlink
    for (Element a : doc.select("a[href]")) {
      String href = a.attr("href");
      if (href.endsWith(".html") || !href.contains("://")) {
        a.unwrap(); // Giữ nội dung text, gỡ bỏ thẻ <a>
      }
    }

    // BƯỚC 2: Cấu hình Flexmark Converter
    MutableDataSet options = new MutableDataSet();

    // Tắt việc sinh thuộc tính ID/Name dạng {#id} trong Markdown output
    options.set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false);
    options.set(FlexmarkHtmlConverter.SKIP_ATTRIBUTES, true);

    // BƯỚC 3: Convert HTML đã làm sạch sang Markdown
    return FlexmarkHtmlConverter.builder(options).build().convert(doc.body().html()).trim();
  }
}
