package tungnn.tutor.java.ast.java;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import java.nio.file.Path;

public class Demo {

  static void main() throws Exception {
    Path src = Path.of("storage/XlsxFileUtil.java");

    ParserConfiguration config =
        new ParserConfiguration()
            .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17)
            .setLexicalPreservationEnabled(true); // Prevent parser re-format source-code

    JavaParser parser = new JavaParser(config);
    ParseResult<CompilationUnit> result = parser.parse(src);

    if (!result.isSuccessful() || result.getResult().isEmpty()) {
      result.getProblems().forEach(System.err::println);
      return;
    }

    CompilationUnit cu = result.getResult().get();
    printAst(cu, 0);
  }

  static void printAst(Node node, int depth) {
    System.out.println("  ".repeat(depth) + node.getClass().getSimpleName());
    for (Node child : node.getChildNodes()) {
      printAst(child, depth + 1);
    }
  }
}
