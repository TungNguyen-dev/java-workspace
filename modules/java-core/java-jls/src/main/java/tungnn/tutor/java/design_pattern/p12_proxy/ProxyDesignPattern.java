void main() {
  Document myDoc = new DocumentProxy("BaoCaoTaiChinh.pdf", "Revenue: 1 billion VND");

  // 1. A regular employee views the document
  System.out.println("--- Access round 1 ---");
  myDoc.displayContent("STAFF");
  myDoc.editContent("STAFF", "Change revenue to 0 VND"); // Will be blocked

  System.out.println("\n--- Access round 2 ---");
  // 2. An administrator accesses it
  myDoc.displayContent("ADMIN");
  myDoc.editContent("ADMIN", "Revenue: 1.5 billion VND"); // Succeeds
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
    System.out.println("Document content: " + content);
  }

  @Override
  public void editContent(String userRole, String newContent) {
    this.content = newContent;
    System.out.println("The document content has been updated.");
  }
}

// Proxy
public class DocumentProxy implements Document {

  private final RealDocument realDocument;
  private final String fileName;

  public DocumentProxy(String fileName, String initialContent) {
    this.fileName = fileName;
    // We can create the real object right away or use lazy loading
    this.realDocument = new RealDocument(initialContent);
  }

  @Override
  public void displayContent(String userRole) {
    // Anyone can see the file name, but viewing the content requires permission
    System.out.println("[Proxy] Accessing file: " + fileName);
    realDocument.displayContent(userRole);
  }

  @Override
  public void editContent(String userRole, String newContent) {
    // BLOCK: only ADMIN may edit
    if ("ADMIN".equalsIgnoreCase(userRole)) {
      realDocument.editContent(userRole, newContent);
    } else {
      System.out.println(
          "[SECURITY] Denied: role '" + userRole + "' is not allowed to edit the document!");
    }
  }
}
