package tungnn.tutor.java.ast.java.util;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class MethodUtil {

  private MethodUtil() {}

  /** Lấy các Method được khai báo trực tiếp trong class. */
  public static List<MethodDeclaration> getDeclaredMethods(ClassOrInterfaceDeclaration typeDecl) {
    if (typeDecl == null) return Collections.emptyList();
    return typeDecl.getMethods();
  }

  /** Lấy các Method được khai báo trực tiếp khớp với bộ Modifier truyền vào. */
  public static List<MethodDeclaration> getDeclaredMethods(
      ClassOrInterfaceDeclaration typeDecl, Modifier... modifiers) {
    if (typeDecl == null) return Collections.emptyList();
    List<Modifier> modifierList = Arrays.asList(modifiers);
    return typeDecl.getMethods().stream()
        .filter(method -> new HashSet<>(method.getModifiers()).containsAll(modifierList))
        .toList();
  }

  /** Lấy tất cả Methods (Declared + Inherited). Yêu cầu JavaParser phải cấu hình SymbolSolver. */
  public static List<ResolvedMethodDeclaration> getAllMethods(
      ClassOrInterfaceDeclaration typeDecl) {
    if (typeDecl == null) return Collections.emptyList();

    ResolvedReferenceTypeDeclaration resolved = typeDecl.resolve();
    return resolved.getAllMethods().stream().map(MethodUsage::getDeclaration).toList();
  }

  /**
   * Tạo chuỗi Chữ ký phương thức (Method Signature). Ví dụ: "public static List<String> getName(int
   * id, String type)"
   */
  public static String parseMethodSignature(MethodDeclaration md) {
    if (md == null) {
      return "";
    }

    StringBuilder builder = new StringBuilder();

    // 1. Modifiers
    if (!md.getModifiers().isEmpty()) {
      builder
          .append(
              md.getModifiers().stream()
                  .map(modifier -> modifier.getKeyword().asString())
                  .collect(Collectors.joining(" ")))
          .append(" ");
    }

    // 2. Type Parameters / Generics
    if (!md.getTypeParameters().isEmpty()) {
      builder
          .append("<")
          .append(
              md.getTypeParameters().stream()
                  .map(TypeParameter::asString)
                  .collect(Collectors.joining(", ")))
          .append("> ");
    }

    // 3. Return Type & Method Name
    builder.append(md.getTypeAsString()).append(" ").append(md.getNameAsString());

    // 4. Parameters
    String params =
        md.getParameters().stream()
            .map(Parameter::getNameAsString)
            .collect(Collectors.joining(", "));

    builder.append("(").append(params).append(")");

    // 5. Throws exceptions
    if (!md.getThrownExceptions().isEmpty()) {
      builder
          .append(" throws ")
          .append(
              md.getThrownExceptions().stream()
                  .map(Type::asString)
                  .collect(Collectors.joining(", ")));
    }

    return builder.toString();
  }

  /**
   * Tạo chuỗi Lời gọi phương thức (Method Invocation). Ví dụ: "void process(String a, int b)" ->
   * "process(a, b)"
   */
  public static String parseMethodInvocation(MethodDeclaration md) {
    if (md == null) {
      return "";
    }

    String args =
        md.getParameters().stream()
            .map(NodeWithSimpleName::getNameAsString)
            .collect(Collectors.joining(", "));

    return md.getNameAsString() + "(" + args + ")";
  }

  /** Lấy danh sách Parameter từ phương thức. */
  public static List<Parameter> getAllMethodParameters(MethodDeclaration md) {
    if (md == null) {
      return Collections.emptyList();
    }

    return md.getParameters();
  }
}
