package tungnn.tutor.java.core.lib.io.stream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ByteStreams {

  static void main() throws IOException {
    byteStream();
  }

  public static void byteStream() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (InputStream in = Files.newInputStream(inPath)) {
      int b;
      while ((b = in.read()) != -1) {
        System.out.print(b + " ");
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (OutputStream out = Files.newOutputStream(outPath)) {
      byte[] data = {72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33};
      for (byte b : data) {
        out.write(b);
      }
    }
  }
}
