package tungnn.tutor.java.tool.hub.plugin.docconv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolResult;

final class ConverterSupport {

    static final String OUTPUT_TYPE = "enum:preview|download";

    private ConverterSupport() {}

    static <T> T withPluginClassLoader(Task<T> task) throws ToolException {
        Objects.requireNonNull(task, "task must not be null");
        Thread t = Thread.currentThread();
        ClassLoader previous = t.getContextClassLoader();
        t.setContextClassLoader(ConverterSupport.class.getClassLoader());
        try {
            return task.run();
        } catch (ToolException e) {
            throw e;
        } catch (Exception e) {
            throw new ToolException("Conversion failed: " + rootMessage(e));
        } finally {
            t.setContextClassLoader(previous);
        }
    }

    static Path toTempFile(ToolContext ctx, ToolFile file, String suffix) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        byte[] content = Objects.requireNonNull(file.content(), "file content must not be null");
        Path dir =
                (ctx != null && ctx.dataDir() != null) ? ctx.dataDir() : Path.of(System.getProperty("java.io.tmpdir"));
        Files.createDirectories(dir);
        String safeSuffix = suffix != null ? suffix : "";
        Path tmp = Files.createTempFile(dir, "upload-", safeSuffix);
        Files.write(tmp, content);
        return tmp;
    }

    static void deleteQuietly(Path p) {
        if (p == null) {
            return;
        }
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }

    static void requireExtension(ToolFile file, String... extensions) throws ToolException {
        if (file == null || file.filename() == null || file.filename().isBlank()) {
            throw new ToolException("Invalid file name");
        }
        String name = file.filename().toLowerCase(Locale.ROOT);
        if (extensions != null) {
            for (String ext : extensions) {
                if (ext != null && name.endsWith(ext.toLowerCase(Locale.ROOT))) {
                    return;
                }
            }
        }
        String expected = extensions == null ? "" : String.join(", ", extensions);
        throw new ToolException("File '" + file.filename() + "' has the wrong format, expected: " + expected);
    }

    static String baseName(String filename) {
        if (filename == null || filename.isBlank()) {
            return "output";
        }
        int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        String name = filename.substring(slash + 1);
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    static ToolResult textResult(String text, Object outputMode, String filename, String contentType) {
        String safeText = text != null ? text : "";
        if ("download".equals(outputMode)) {
            return ToolResult.ok(
                    new ToolFile(filename, contentType + "; charset=UTF-8", safeText.getBytes(StandardCharsets.UTF_8)));
        }
        return ToolResult.ok(safeText);
    }

    private static String rootMessage(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String msg = root.getMessage();
        return root.getClass().getSimpleName() + (msg == null || msg.isBlank() ? "" : ": " + msg);
    }

    @FunctionalInterface
    interface Task<T> {
        T run() throws Exception;
    }
}
