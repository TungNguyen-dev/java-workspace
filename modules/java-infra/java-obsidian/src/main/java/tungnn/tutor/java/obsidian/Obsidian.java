package tungnn.tutor.java.obsidian;

import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.config.ObsidianConfig;
import tungnn.tutor.java.obsidian.note.NoteService;
import tungnn.tutor.java.obsidian.report.IndexNoteGenerator;
import tungnn.tutor.java.obsidian.report.ReportPublisher;
import tungnn.tutor.java.obsidian.report.ReportService;
import tungnn.tutor.java.obsidian.template.TemplateService;
import tungnn.tutor.java.obsidian.vault.VaultService;

public final class Obsidian {

    private final ObsidianConfig config;
    private final NoteService notes;
    private final TemplateService templates;
    private final VaultService vault;
    private final ReportService reports;
    private final ReportPublisher publisher;
    private final IndexNoteGenerator indexes;

    private Obsidian(ObsidianCli cli, ObsidianConfig config) {
        this.config = config;
        this.notes = new NoteService(cli);
        this.vault = new VaultService(cli);
        this.templates = new TemplateService(cli, notes, vault::root, config.templatesFolder());
        this.reports = new ReportService(vault);
        this.publisher = new ReportPublisher(notes, config.reportsFolder());
        this.indexes = new IndexNoteGenerator(vault, publisher);
    }

    public static Obsidian connect(ObsidianConfig config) {
        return new Obsidian(ObsidianCli.create(config), config);
    }

    public static Obsidian fromEnv() {
        return connect(ObsidianConfig.fromEnv());
    }

    public ObsidianConfig config() {
        return config;
    }

    public NoteService notes() {
        return notes;
    }

    public TemplateService templates() {
        return templates;
    }

    public VaultService vault() {
        return vault;
    }

    public ReportService reports() {
        return reports;
    }

    public ReportPublisher publisher() {
        return publisher;
    }

    public IndexNoteGenerator indexes() {
        return indexes;
    }
}
