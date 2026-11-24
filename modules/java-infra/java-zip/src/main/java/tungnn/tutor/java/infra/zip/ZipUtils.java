package tungnn.tutor.java.infra.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public final class ZipUtils {

    private ZipUtils() {
        // Utility class, prevent instantiation
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static void zipFile(Path sourceFile, Path targetZip) throws IOException {
        validateSource(sourceFile);
        try (var zipFile = new ZipFile(targetZip.toFile())) {
            zipFile.addFile(sourceFile.toFile());
        }
    }

    public static void zipFiles(List<Path> sourceFiles, Path targetZip) throws IOException {
        if (sourceFiles == null || sourceFiles.isEmpty()) {
            throw new IllegalArgumentException("Source file list cannot be null or empty.");
        }
        List<File> files = sourceFiles.stream()
                .peek(ZipUtils::validateSource)
                .map(Path::toFile)
                .collect(Collectors.toList());

        try (var zipFile = new ZipFile(targetZip.toFile())) {
            zipFile.addFiles(files);
        }
    }

    public static void zipFolder(Path sourceFolder, Path targetZip) throws IOException {
        validateSource(sourceFolder);
        if (!Files.isDirectory(sourceFolder)) {
            throw new IllegalArgumentException("Source path must be a directory.");
        }
        try (var zipFile = new ZipFile(targetZip.toFile())) {
            zipFile.addFolder(sourceFolder.toFile());
        }
    }

    public static void zipEncrypted(Path source, Path targetZip, char[] password) throws IOException {
        validateSource(source);
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        var parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.AES);
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

        try (var zipFile = new ZipFile(targetZip.toFile(), password)) {
            if (Files.isDirectory(source)) {
                zipFile.addFolder(source.toFile(), parameters);
            } else {
                zipFile.addFile(source.toFile(), parameters);
            }
        }
    }

    public static void extract(Path targetZip, Path destination) throws IOException {
        validateSource(targetZip);
        Files.createDirectories(destination);

        try (var zipFile = new ZipFile(targetZip.toFile())) {
            zipFile.extractAll(destination.toString());
        }
    }

    public static void extractEncrypted(Path targetZip, Path destination, char[] password) throws IOException {
        validateSource(targetZip);
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Extraction password cannot be null or empty.");
        }
        Files.createDirectories(destination);

        try (var zipFile = new ZipFile(targetZip.toFile(), password)) {
            zipFile.extractAll(destination.toString());
        }
    }

    private static void validateSource(Path path) {
        if (path == null || !Files.exists(path)) {
            throw new IllegalArgumentException("Source path does not exist: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new IllegalStateException("Permission denied to read file/directory: " + path);
        }
    }
}
