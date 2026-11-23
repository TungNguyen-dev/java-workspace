package tungnn.tutor.java.ast.java.util;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class FieldUtil {

  private FieldUtil() {}

  /** Lấy các Field được khai báo trực tiếp trong class. */
  public static List<FieldDeclaration> getDeclaredFields(ClassOrInterfaceDeclaration typeDecl) {
    if (typeDecl == null) return Collections.emptyList();
    return typeDecl.getFields();
  }

  /** Lấy các Field được khai báo trực tiếp khớp với bộ Modifier truyền vào. */
  public static List<FieldDeclaration> getDeclaredFields(
      ClassOrInterfaceDeclaration typeDecl, Modifier... modifiers) {
    if (typeDecl == null) return Collections.emptyList();
    List<Modifier> modifierList = Arrays.asList(modifiers);
    return typeDecl.getFields().stream()
        .filter(field -> new HashSet<>(field.getModifiers()).containsAll(modifierList))
        .toList();
  }

  /** Lấy tất cả Fields (Declared + Inherited). Yêu cầu JavaParser phải cấu hình SymbolSolver. */
  public static List<ResolvedFieldDeclaration> getAllFields(ClassOrInterfaceDeclaration typeDecl) {
    if (typeDecl == null) return Collections.emptyList();

    ResolvedReferenceTypeDeclaration resolved = typeDecl.resolve();
    return resolved.getAllFields();
  }
}
