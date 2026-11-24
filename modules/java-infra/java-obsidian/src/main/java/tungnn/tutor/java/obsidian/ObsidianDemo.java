package tungnn.tutor.java.obsidian;

public class ObsidianDemo {

    static void main() {
        System.out.println(ObsidianUtils.createNote(
                "Sandbox/Java Obsidian Test.md", "Java Obsidian Test", "Line 1\nLine 2\\end", "java"));

        ObsidianUtils.saveTemplate("Sandbox Template", "# {{title}}\n\nCreated {{date}}\n");
        System.out.println(ObsidianUtils.createNoteFromTemplate("Sandbox Template", "Sandbox/From Template.md"));

        System.out.println(ObsidianUtils.publishVaultStats());
        System.out.println(ObsidianUtils.publishLinkHealth());
        System.out.println(ObsidianUtils.publishWeeklyReport());
        System.out.println(ObsidianUtils.generateIndex("Sandbox"));

        System.out.println(ObsidianUtils.readNote("Sandbox/Java Obsidian Test.md"));

        try {
            System.out.println(ObsidianUtils.publishDailyReport());
        } catch (tungnn.tutor.java.obsidian.cli.ObsidianCliException e) {
            System.out.println("Daily report skipped: " + e.getMessage());
        }
    }
}
