void main() {
  Document myDoc = new DocumentProxy("BaoCaoTaiChinh.pdf", "Doanh thu: 1 tỷ VNĐ");

  // 1. Nhân viên bình thường xem tài liệu
  System.out.println("--- Lượt truy cập 1 ---");
  myDoc.displayContent("STAFF");
  myDoc.editContent("STAFF", "Sửa doanh thu thành 0 VNĐ"); // Sẽ bị chặn

  System.out.println("\n--- Lượt truy cập 2 ---");
  // 2. Quản trị viên truy cập
  myDoc.displayContent("ADMIN");
  myDoc.editContent("ADMIN", "Doanh thu: 1.5 tỷ VNĐ"); // Thành công
}

// Subject
public interface Document {

  void displayContent(String userRole);

  void editContent(String userRole, String newContent);
}

// Concrete Subject
class RealDocument implements Document {

  private String content;

  public RealDocument(String content) {
    this.content = content;
  }

  @Override
  public void displayContent(String userRole) {
    System.out.println("Nội dung tài liệu: " + content);
  }

  @Override
  public void editContent(String userRole, String newContent) {
    this.content = newContent;
    System.out.println("Tài liệu đã được cập nhật nội dung mới.");
  }
}

// Proxy
public class DocumentProxy implements Document {

  private RealDocument realDocument;
  private String fileName;

  public DocumentProxy(String fileName, String initialContent) {
    this.fileName = fileName;
    // Chúng ta có thể khởi tạo đối tượng thật ngay hoặc dùng Lazy Loading
    this.realDocument = new RealDocument(initialContent);
  }

  @Override
  public void displayContent(String userRole) {
    // Bất kỳ ai cũng có thể xem tên file, nhưng xem nội dung thì cần quyền
    System.out.println("[Proxy] Đang truy cập file: " + fileName);
    realDocument.displayContent(userRole);
  }

  @Override
  public void editContent(String userRole, String newContent) {
    // CHẶN: Chỉ ADMIN mới được sửa
    if ("ADMIN".equalsIgnoreCase(userRole)) {
      realDocument.editContent(userRole, newContent);
    } else {
      System.out.println(
          "[BẢO MẬT] Từ chối: Quyền '" + userRole + "' không được phép sửa tài liệu!");
    }
  }
}
