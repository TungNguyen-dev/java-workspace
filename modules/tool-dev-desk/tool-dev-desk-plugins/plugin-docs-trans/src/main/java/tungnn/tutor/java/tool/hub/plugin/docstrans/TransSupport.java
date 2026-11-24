package tungnn.tutor.java.tool.hub.plugin.docstrans;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.memory.CsvFileTextTranslationMemory;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolException;

final class TransSupport {
    static final String LANGUAGE_TYPE;
    private static final Map<Path, TextTranslationMemory> MEMORIES = new ConcurrentHashMap<>();

    static {
        StringBuilder sb = new StringBuilder("enum:");
        for (LanguageCode c : LanguageCode.values()) {
            if (sb.length() > 5) sb.append('|');
            sb.append(c.name());
        }
        LANGUAGE_TYPE = sb.toString();
    }

    private TransSupport() {}

    static <T> T withPluginClassLoader(Task<T> task) throws ToolException {
        Thread t = Thread.currentThread();
        ClassLoader previous = t.getContextClassLoader();
        t.setContextClassLoader(TransSupport.class.getClassLoader());
        try {
            return task.run();
        } catch (ToolException e) {
            throw e;
        } catch (Exception e) {
            throw new ToolException("Translation failed: " + rootMessage(e));
        } catch (LinkageError e) {
            throw new ToolException("Library compatibility error in plugin: " + rootMessage(e));
        } finally {
            t.setContextClassLoader(previous);
        }
    }

    static Path dataDir(ToolContext ctx) {
        return ctx != null && ctx.dataDir() != null
                ? ctx.dataDir()
                : Path.of(System.getProperty("java.io.tmpdir"), "docs-trans");
    }

    static TextTranslationMemory memory(ToolContext ctx) {
        Path dir = dataDir(ctx).resolve("memory").toAbsolutePath().normalize();
        return MEMORIES.computeIfAbsent(dir, CsvFileTextTranslationMemory::new);
    }

    static LanguageCode language(Object value) throws ToolException {
        try {
            return LanguageCode.valueOf(String.valueOf(value).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ToolException("Unsupported language: " + value);
        }
    }

    static String str(Object value) {
        return value == null ? null : value.toString();
    }

    static void deleteRecursively(Path dir) {
        if (dir == null || !Files.exists(dir)) return;
        try (var s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        } catch (IOException ignored) {
        }
    }

    static String rootMessage(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String msg = root.getMessage();
        return root.getClass().getSimpleName() + (msg == null ? "" : ": " + msg);
    }

    @FunctionalInterface
    interface Task<T> {
        T run() throws Exception;
    }
}
