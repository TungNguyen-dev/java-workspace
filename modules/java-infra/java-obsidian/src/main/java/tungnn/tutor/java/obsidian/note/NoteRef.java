package tungnn.tutor.java.obsidian.note;

import tungnn.tutor.java.obsidian.cli.ObsidianCommand;

public sealed interface NoteRef {

    ObsidianCommand applyTo(ObsidianCommand command);

    static NoteRef byName(String file) {
        return new ByName(file);
    }

    static NoteRef byPath(String path) {
        return new ByPath(path);
    }

    record ByName(String file) implements NoteRef {
        @Override
        public ObsidianCommand applyTo(ObsidianCommand command) {
            return command.param("file", file);
        }
    }

    record ByPath(String path) implements NoteRef {
        @Override
        public ObsidianCommand applyTo(ObsidianCommand command) {
            return command.param("path", path);
        }
    }
}
