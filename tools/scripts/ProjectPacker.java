import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class ProjectPacker {

  private static final String FILE_BOUNDARY_START = "--_--_BOUNDARY_START_--_--";
  private static final String FILE_BOUNDARY_END = "--_--_BOUNDARY_END_--_--";
  private static final String FILE_PATH_HEADER = "File-Path: ";

  public static void main(String[] args) {
    if (args.length < 3) {
      printUsage();
      return;
    }

    String mode = args[0];

    try {
      if ("merge".equalsIgnoreCase(mode)) {
        if (args.length != 3) {
          printUsage();
          return;
        }
        Path sourceDir = Paths.get(args[1]);
        Path outputFile = Paths.get(args[2]);
        mergeProject(sourceDir, outputFile);
      } else if ("split".equalsIgnoreCase(mode)) {
        if (args.length != 3) {
          printUsage();
          return;
        }
        Path inputFile = Paths.get(args[1]);
        Path outputDir = Paths.get(args[2]);
        splitProject(inputFile, outputDir);
      } else {
        System.err.println("Error: Invalid mode '" + mode + "'.");
        printUsage();
      }
    } catch (IOException e) {
      System.err.println("An I/O error occurred: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void printUsage() {
    System.err.println("Usage:");
    System.err.println(" To merge: java ProjectPacker merge <source_directory> <output_file>");
    System.err.println(" To split: java ProjectPacker split <input_file> <destination_directory>");
    System.err.println("\nExamples:");
    System.err.println(" java ProjectPacker merge ./my-java-project merged.pack");
    System.err.println(" java ProjectPacker split merged.pack ./restored-project");
  }

  /**
   * Merges all .java files from a source directory into a single output file.
   *
   * @param sourceDir The root directory of the project to merge.
   * @param outputFile The single file to write the merged content to.
   * @throws IOException if an I/O error occurs.
   */
  public static void mergeProject(Path sourceDir, Path outputFile) throws IOException {
    if (!Files.isDirectory(sourceDir)) {
      throw new IOException("Source directory does not exist: " + sourceDir);
    }

    System.out.println("Starting to merge from '" + sourceDir + "' into '" + outputFile + "'...");

    try (BufferedWriter writer =
            Files.newBufferedWriter(
                outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Stream<Path> paths = Files.walk(sourceDir)) {

      paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".java"))
          .forEach(filePath -> {
            try {
              Path relativePath = sourceDir.relativize(filePath);
              System.out.println("Processing: " + relativePath);

              writer.write(FILE_BOUNDARY_START);
              writer.newLine();
              writer.write(
                  FILE_PATH_HEADER
                      + relativePath.toString().replace('\\', '/')); // Normalize path separator
              writer.newLine();

              // Read the entire file content into a String and write it to the writer.
              String content = Files.readString(filePath);
              writer.write(content);

              // Ensure the end boundary is always on a new line, even if the source file
              // doesn't end with one.
              if (!content.endsWith("\n") && !content.endsWith("\r\n")) {
                writer.newLine();
              }

              writer.write(FILE_BOUNDARY_END);
              writer.newLine();
            } catch (IOException e) {
              throw new RuntimeException("Error processing file: " + filePath, e);
            }
          });
    } catch (RuntimeException e) {
      // Unwrap the IOException from the RuntimeException thrown in the lambda
      throw new IOException(e.getCause());
    }

    System.out.println("Merge process completed successfully!");
  }

  /**
   * Splits a merged file back into its original directory and file structure.
   *
   * @param inputFile The merged file to read from.
   * @param outputDir The destination directory to restore the project to.
   * @throws IOException if an I/O error occurs.
   */
  public static void splitProject(Path inputFile, Path outputDir) throws IOException {
    if (!Files.isRegularFile(inputFile)) {
      throw new IOException("Input file does not exist: " + inputFile);
    }

    System.out.println("Starting to split from '" + inputFile + "' into '" + outputDir + "'...");

    try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
      String line;
      StringBuilder fileContent = null;
      Path currentOutputFile = null;

      while ((line = reader.readLine()) != null) {
        if (line.equals(FILE_BOUNDARY_START)) {
          // Start of a new file block. The next line should be the path header.
          String pathLine = reader.readLine();
          if (pathLine != null && pathLine.startsWith(FILE_PATH_HEADER)) {
            String relativePathStr = pathLine.substring(FILE_PATH_HEADER.length());
            currentOutputFile = outputDir.resolve(relativePathStr);
            fileContent = new StringBuilder();
            System.out.println("Restoring: " + currentOutputFile);
          }
        } else if (line.equals(FILE_BOUNDARY_END)) {
          // End of the current file block. Write the content to disk.
          if (currentOutputFile != null && fileContent != null) {
            // Create parent directories if they don't exist
            Files.createDirectories(currentOutputFile.getParent());
            // Write the accumulated content to the file
            Files.writeString(
                currentOutputFile,
                fileContent.toString(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

            // Reset state for the next file
            currentOutputFile = null;
            fileContent = null;
          }
        } else if (fileContent != null) {
          // We are inside a file block, so append the current line.
          if (fileContent.length() > 0) {
            fileContent.append(System.lineSeparator());
          }
          fileContent.append(line);
        }
      }
    }

    System.out.println("Split process completed successfully!");
  }
}
