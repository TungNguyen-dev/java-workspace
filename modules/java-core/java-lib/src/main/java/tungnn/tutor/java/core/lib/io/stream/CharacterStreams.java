package tungnn.tutor.java.core.lib.io.stream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class CharacterStreams {

  static void main() throws IOException {
    characterStreams();
    characterStreamBasedOnByteStream();
  }

  public static void characterStreams() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (Reader reader = new FileReader(inPath.toFile())) {
      int c;
      while ((c = reader.read()) != -1) {
        System.out.print((char) c);
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (Writer writer = new FileWriter(outPath.toFile())) {
      char[] chars = {'H', 'e', 'l', 'l', 'o'};
      for (char c : chars) {
        writer.write(c);
      }
    }
  }

  public static void characterStreamBasedOnByteStream() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (Reader reader = new InputStreamReader(Files.newInputStream(inPath))) {
      int c;
      while ((c = reader.read()) != -1) {
        System.out.print((char) c);
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outPath))) {
      char[] chars = {'H', 'e', 'l', 'l', 'o'};
      for (char c : chars) {
        writer.write(c);
      }
    }
  }
}
