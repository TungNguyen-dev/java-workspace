import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

void main(String[] args) {
  if (args.length == 0) {
    System.out.println("Please pass a directory path. Example: java Main.java /path/to/dir");
    return;
  }

  Path dirPath = Paths.get(args[0]);

  if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
    System.err.println("Error: the given path does not exist or is not a directory.");
    return;
  }

  // Walk through all files in the directory
  try (Stream<Path> stream = Files.walk(dirPath)) {
    stream
        .filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".md"))
        .forEach(file -> extractUrlsFromFile(file));
  } catch (IOException e) {
    System.err.println("Error while walking the directory: " + e.getMessage());
  }
}

void extractUrlsFromFile(Path filePath) {
  String regex = "\\b(https?://|www\\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
  Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

  try {
    String content = Files.readString(filePath);
    Matcher matcher = pattern.matcher(content);

    List<String> urls = new ArrayList<>();
    while (matcher.find()) {
      urls.add(matcher.group());
    }
    urls =
        urls.stream().filter(url -> url.contains("youtube") || url.contains("coursera")).toList();

    if (!urls.isEmpty()) {
      System.out.println("\n[File]: " + filePath.toAbsolutePath());
      urls.forEach(url -> System.out.println(url));
    }
  } catch (IOException e) {
    System.err.println("Unable to read file " + filePath + ": " + e.getMessage());
  }
}
