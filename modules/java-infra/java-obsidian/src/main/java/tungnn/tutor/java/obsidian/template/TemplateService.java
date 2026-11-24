package tungnn.tutor.java.obsidian.template;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import tungnn.tutor.java.jackson.v3.JsonUtil;
import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.cli.ObsidianCommand;
import tungnn.tutor.java.obsidian.note.NoteService;

public class TemplateService {

    private final ObsidianCli cli;
    private final NoteService notes;
    private final Supplier<Path> vaultRoot;
    private final String configuredFolder;

    public TemplateService(ObsidianCli cli, NoteService notes, Supplier<Path> vaultRoot, String configuredFolder) {
        this.cli = cli;
        this.notes = notes;
        this.vaultRoot = vaultRoot;
        this.configuredFolder = configuredFolder;
    }

    public String templatesFolder() {
        if (configuredFolder != null) {
            return configuredFolder;
        }
        var file = vaultRoot.get().resolve(".obsidian").resolve("templates.json");
        if (Files.isRegularFile(file)) {
            var folder = JsonUtil.createJsonMapper()
                    .readTree(tungnn.tutor.java.core.lib.io.filesystem.FileUtils.readString(file))
                    .path("folder")
                    .asString("");
            if (!folder.isBlank()) {
                return folder;
            }
        }
        throw new IllegalStateException(
                "Templates folder is unknown: set OBSIDIAN_TEMPLATES_FOLDER or configure it in Obsidian's Templates"
                        + " plugin");
    }

    public List<String> list() {
        return cli.executeLines(ObsidianCommand.of("templates"));
    }

    public String read(String name, boolean resolve) {
        return cli.execute(
                ObsidianCommand.of("template:read").param("name", name).flagIf(resolve, "resolve"));
    }

    public void create(String name, String content) {
        cli.execute(ObsidianCommand.of("create")
                .param("path", templatesFolder() + "/" + name + ".md")
                .content(content)
                .flag("overwrite"));
    }

    public void createNoteFrom(String template, String path) {
        notes.createFromTemplate(path, template);
    }
}
