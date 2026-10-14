package tungnn.tutor.java.ast.java.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class TypeUtil {

  private TypeUtil() {}

  /**
   * Lấy tất cả các khai báo Class hoặc Interface có trong CompilationUnit (bao gồm cả top-level
   * classes và inner/nested classes).
   */
  public static List<ClassOrInterfaceDeclaration> getAllClassOrInterfaceDeclarations(
      CompilationUnit cu) {
    if (cu == null) {
      return Collections.emptyList();
    }
    return cu.findAll(ClassOrInterfaceDeclaration.class);
  }

  /**
   * Lấy danh sách các Class hoặc Interface được khai báo ở tầng cao nhất (Top-Level) của
   * CompilationUnit (bỏ qua các inner / nested classes).
   */
  public static List<ClassOrInterfaceDeclaration> getAllTopLevelClassOrInterfaceDeclarations(
      CompilationUnit cu) {
    if (cu == null) {
      return Collections.emptyList();
    }

    return cu.getTypes().stream()
        .filter(TypeDeclaration::isClassOrInterfaceDeclaration)
        .map(TypeDeclaration::asClassOrInterfaceDeclaration)
        .toList();
  }

  /**
   * Lấy khai báo Class hoặc Interface chính (Primary Class/Interface) của CompilationUnit.
   *
   * <p>Thuật toán tìm kiếm: 1. Lớp top-level có modifier 'public'. 2. Lớp top-level có tên trùng
   * với tên file (.java). 3. Lớp top-level đầu tiên tìm thấy trong file.
   */
  public static ClassOrInterfaceDeclaration getPrimaryClassOrInterfaceDeclaration(
      CompilationUnit cu) {
    if (cu == null) {
      return null;
    }

    List<ClassOrInterfaceDeclaration> topLevelClasses =
        cu.getTypes().stream()
            .filter(TypeDeclaration::isClassOrInterfaceDeclaration)
            .map(TypeDeclaration::asClassOrInterfaceDeclaration)
            .toList();

    if (topLevelClasses.isEmpty()) {
      return null;
    }

    for (ClassOrInterfaceDeclaration decl : topLevelClasses) {
      if (decl.isPublic()) {
        return decl;
      }
    }

    String primaryTypeName = cu.getPrimaryTypeName().orElse(null);
    if (primaryTypeName != null) {
      for (ClassOrInterfaceDeclaration decl : topLevelClasses) {
        if (decl.getNameAsString().equals(primaryTypeName)) {
          return decl;
        }
      }
    }

    return topLevelClasses.getFirst();
  }

  // ==========================================
  // JAVADOC HELPERS (Dùng chung cho Type, Method, Field)
  // ==========================================

  /** Trích xuất JavadocComment (AST Node) từ một Node bất kỳ. */
  public static Optional<JavadocComment> getJavadocComment(Node node) {
    if (node == null) {
      return Optional.empty();
    }

    if (node instanceof NodeWithJavadoc<?> nodeWithJavadoc) {
      return nodeWithJavadoc.getJavadocComment();
    }

    return node.getComment().filter(Comment::isJavadocComment).map(Comment::asJavadocComment);
  }

  /** Trích xuất nội dung văn bản của Javadoc (đã được parse và làm sạch). */
  public static Optional<String> getJavadocText(Node node) {
    return getJavadocComment(node).map(javadocComment -> javadocComment.parse().toText().trim());
  }

  /** Trích xuất nội dung thô (raw content) của Javadoc. */
  public static Optional<String> getRawJavadocText(Node node) {
    return getJavadocComment(node).map(JavadocComment::getContent);
  }
}
