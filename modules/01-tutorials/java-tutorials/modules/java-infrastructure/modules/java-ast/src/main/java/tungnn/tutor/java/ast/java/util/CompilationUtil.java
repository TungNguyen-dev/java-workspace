package tungnn.tutor.java.ast.java.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.ast.java.JavaParsingException;

public final class CompilationUtil {

  private CompilationUtil() {}

  public static CompilationUnit parseCompilationUnit(JavaParser parser, Path path) {
    try {
      return parser.parse(path).getResult().orElseThrow();
    } catch (IOException e) {
      throw new JavaParsingException("Failed to parse file: " + path.toString());
    }
  }
}
