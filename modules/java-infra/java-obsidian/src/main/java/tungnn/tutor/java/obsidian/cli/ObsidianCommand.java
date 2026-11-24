package tungnn.tutor.java.obsidian.cli;

import java.util.ArrayList;
import java.util.List;

public final class ObsidianCommand {

    private final String name;
    private final List<String> params = new ArrayList<>();
    private final List<String> flags = new ArrayList<>();

    private ObsidianCommand(String name) {
        this.name = name;
    }

    public static ObsidianCommand of(String name) {
        return new ObsidianCommand(name);
    }

    public ObsidianCommand param(String key, String value) {
        if (value != null) {
            params.add(key + "=" + value);
        }
        return this;
    }

    public ObsidianCommand content(String value) {
        return param("content", CliValueEscaper.escape(value));
    }

    public ObsidianCommand flag(String flag) {
        flags.add(flag);
        return this;
    }

    public ObsidianCommand flagIf(boolean condition, String flag) {
        return condition ? flag(flag) : this;
    }

    public List<String> toArgs(String vault) {
        var args = new ArrayList<String>();
        if (vault != null && !vault.isBlank()) {
            args.add("vault=" + vault);
        }
        args.add(name);
        args.addAll(params);
        args.addAll(flags);
        return args;
    }
}
