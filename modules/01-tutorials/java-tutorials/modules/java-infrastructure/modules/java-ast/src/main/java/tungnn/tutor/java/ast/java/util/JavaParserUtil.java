package tungnn.tutor.java.ast.java.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class JavaParserUtil {

  private JavaParserUtil() {}

  /**
   * Tạo JavaParser mặc định với cấu hình cơ bản:
   *
   * <ul>
   *   <li>Ngôn ngữ: Mặc định theo JavaParser (LanguageLevel.BLEEDING_EDGE hoặc JAVA mới nhất)
   *   <li>Encoding: UTF-8
   *   <li>SymbolResolver: Chỉ hỗ trợ các class trong JDK chuẩn (ReflectionTypeSolver)
   * </ul>
   */
  public static JavaParser createDefaultParser() {
    return createAdvancedParser(ParserConfiguration.LanguageLevel.JAVA_25, false, (Path[]) null);
  }

  /**
   * Tạo JavaParser nâng cao cho phép tùy chỉnh Language Level, Lexical Preservation và đăng ký
   * SymbolResolver cho source code dự án.
   *
   * @param languageLevel Phiên bản Java (ví dụ: LanguageLevel.JAVA_25)
   * @param enableLexicalPreservation Bật/Tắt tính năng giữ nguyên định dạng code gốc (spaces,
   *     comments, layout)
   * @param sourcePaths Danh sách đường dẫn source code (.java) để SymbolSolver resolve liên kết nội
   *     bộ
   */
  public static JavaParser createAdvancedParser(
      ParserConfiguration.LanguageLevel languageLevel,
      boolean enableLexicalPreservation,
      Path... sourcePaths) {

    ParserConfiguration config = new ParserConfiguration();
    config.setLanguageLevel(
        languageLevel != null ? languageLevel : ParserConfiguration.LanguageLevel.JAVA_25);
    config.setLexicalPreservationEnabled(enableLexicalPreservation);
    config.setCharacterEncoding(StandardCharsets.UTF_8);

    // 1. Khởi tạo CombinedTypeSolver
    CombinedTypeSolver typeSolver = new CombinedTypeSolver();

    // Hỗ trợ resolve các class thuộc JDK chuẩn (String, List, Object...)
    typeSolver.add(new ReflectionTypeSolver());

    // Hỗ trợ resolve các class nội bộ trong dự án từ các source paths
    if (sourcePaths != null) {
      for (Path srcPath : sourcePaths) {
        if (srcPath != null) {
          typeSolver.add(new JavaParserTypeSolver(srcPath));
        }
      }
    }

    // 2. Gắn SymbolSolver vào ParserConfiguration
    JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
    config.setSymbolResolver(symbolSolver);

    return new JavaParser(config);
  }

  /**
   * Shortcut tạo Advanced Parser mặc định Java 25 với Lexical Preservation bật sẵn.
   *
   * @deprecated Khuyên dùng {@link #createAdvancedParser(ParserConfiguration.LanguageLevel,
   *     boolean, Path...)}
   */
  @Deprecated
  public static JavaParser createJavaParserVersion25(Path... sourcePaths) {
    return createAdvancedParser(ParserConfiguration.LanguageLevel.JAVA_25, true, sourcePaths);
  }
}
