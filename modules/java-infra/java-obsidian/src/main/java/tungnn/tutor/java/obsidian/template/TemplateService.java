package tungnn.tutor.java.obsidian.template;

import java.util.List;
import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.cli.ObsidianCommand;
import tungnn.tutor.java.obsidian.note.NoteService;

public class TemplateService {

    private final ObsidianCli cli;
    private final NoteService notes;
    private final String templatesFolder;

    public TemplateService(ObsidianCli cli, NoteService notes, String templatesFolder) {
        this.cli = cli;
        this.notes = notes;
        this.templatesFolder = templatesFolder;
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
                .param("path", templatesFolder + "/" + name + ".md")
                .content(content)
                .flag("overwrite"));
    }

    public void createNoteFrom(String template, String path) {
        notes.createFromTemplate(path, template);
    }
}
