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

  public static List<ClassOrInterfaceDeclaration> getAllClassOrInterfaceDeclarations(
      CompilationUnit cu) {
    if (cu == null) {
      return Collections.emptyList();
    }
    return cu.findAll(ClassOrInterfaceDeclaration.class);
  }

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
  // JAVADOC HELPERS (shared by Type, Method, Field)
  // ==========================================

  public static Optional<JavadocComment> getJavadocComment(Node node) {
    if (node == null) {
      return Optional.empty();
    }

    if (node instanceof NodeWithJavadoc<?> nodeWithJavadoc) {
      return nodeWithJavadoc.getJavadocComment();
    }

    return node.getComment().filter(Comment::isJavadocComment).map(Comment::asJavadocComment);
  }

  public static Optional<String> getJavadocText(Node node) {
    return getJavadocComment(node).map(javadocComment -> javadocComment.parse().toText().trim());
  }

  public static Optional<String> getRawJavadocText(Node node) {
    return getJavadocComment(node).map(JavadocComment::getContent);
  }
}
