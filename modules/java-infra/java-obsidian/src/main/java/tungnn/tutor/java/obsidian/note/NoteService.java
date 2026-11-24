package tungnn.tutor.java.obsidian.note;

import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.cli.ObsidianCommand;

public class NoteService {

    static final int MAX_SINGLE_CALL_CHARS = 200_000;

    private final ObsidianCli cli;

    public NoteService(ObsidianCli cli) {
        this.cli = cli;
    }

    public void create(NoteDraft draft, boolean overwrite) {
        var markdown = draft.toMarkdown();
        var head = markdown.length() <= MAX_SINGLE_CALL_CHARS ? markdown : markdown.substring(0, MAX_SINGLE_CALL_CHARS);

        cli.execute(ObsidianCommand.of("create")
                .param("path", draft.path())
                .content(head)
                .flagIf(overwrite, "overwrite"));

        // Large notes exceed the OS argument size limit, so the remainder is appended in chunks
        for (var offset = head.length(); offset < markdown.length(); offset += MAX_SINGLE_CALL_CHARS) {
            var end = Math.min(offset + MAX_SINGLE_CALL_CHARS, markdown.length());
            cli.execute(ObsidianCommand.of("append")
                    .param("path", draft.path())
                    .content(markdown.substring(offset, end))
                    .flag("inline"));
        }
    }

    public void createFromTemplate(String path, String template) {
        cli.execute(ObsidianCommand.of("create").param("path", path).param("template", template));
    }

    public String read(NoteRef ref) {
        return cli.execute(ref.applyTo(ObsidianCommand.of("read")));
    }

    public void append(NoteRef ref, String content, String heading) {
        cli.execute(ref.applyTo(ObsidianCommand.of("append")).content(content).param("heading", heading));
    }

    public void appendToDaily(String content, String heading) {
        cli.execute(ObsidianCommand.of("daily:append").content(content).param("heading", heading));
    }

    public void prepend(NoteRef ref, String content, String heading) {
        cli.execute(ref.applyTo(ObsidianCommand.of("prepend")).content(content).param("heading", heading));
    }

    public void move(NoteRef ref, String to) {
        cli.execute(ref.applyTo(ObsidianCommand.of("move")).param("to", to));
    }

    public void rename(NoteRef ref, String name) {
        cli.execute(ref.applyTo(ObsidianCommand.of("rename")).param("name", name));
    }

    public void delete(NoteRef ref, boolean permanent) {
        cli.execute(ref.applyTo(ObsidianCommand.of("delete")).flagIf(permanent, "permanent"));
    }

    public void setProperty(NoteRef ref, String name, String value, String type) {
        cli.execute(ref.applyTo(ObsidianCommand.of("property:set"))
                .param("name", name)
                .param("value", value)
                .param("type", type));
    }

    public String readProperty(NoteRef ref, String name) {
        return cli.execute(ref.applyTo(ObsidianCommand.of("property:read")).param("name", name))
                .strip();
    }
}
