package tungnn.tutor.java.core.lib.io.stream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedStreams {

  static void main() throws IOException {
    bufferedByteStream();
    bufferedCharacterStream();
    lineOrientedIO();
    flushingBufferedStream();
  }

  public static void bufferedByteStream() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (InputStream in = Files.newInputStream(inPath);
        BufferedInputStream buffIn = new BufferedInputStream(in); ) {
      int b;
      while ((b = buffIn.read()) != -1) {
        System.out.print(b + " ");
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (OutputStream out = Files.newOutputStream(outPath);
        BufferedOutputStream buffOut = new BufferedOutputStream(out); ) {
      byte[] data = {72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100, 33};
      for (byte b : data) {
        buffOut.write(b);
      }
    }
  }

  public static void bufferedCharacterStream() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (FileReader fileReader = new FileReader(inPath.toFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader)) {
      int c;
      while ((c = bufferedReader.read()) != -1) {
        System.out.print((char) c);
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (FileWriter fileWriter = new FileWriter(outPath.toFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
      char[] chars = {'H', 'e', 'l', 'l', 'o'};
      for (char c : chars) {
        bufferedWriter.write(c);
      }
    }
  }

  public static void lineOrientedIO() throws IOException {
    Path inPath = Path.of("storage", "input.txt");
    try (BufferedReader bufferedReader = Files.newBufferedReader(inPath)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        System.out.println(line);
      }
    }

    Path outPath = Path.of("storage", "output.txt");
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outPath)) {
      String line = "Hello, World!";
      bufferedWriter.write(line);
    }
  }

  static void flushingBufferedStream() throws IOException {
    Path outPath = Path.of("storage", "output.txt");
    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outPath)) {
      String line = "Hello, World!";
      bufferedWriter.write(line);
      bufferedWriter.flush();
    }
  }
}
