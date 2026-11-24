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

  public static JavaParser createDefaultParser() {
    return createAdvancedParser(ParserConfiguration.LanguageLevel.JAVA_25, false, (Path[]) null);
  }

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

  @Deprecated
  public static JavaParser createJavaParserVersion25(Path... sourcePaths) {
    return createAdvancedParser(ParserConfiguration.LanguageLevel.JAVA_25, true, sourcePaths);
  }
}
