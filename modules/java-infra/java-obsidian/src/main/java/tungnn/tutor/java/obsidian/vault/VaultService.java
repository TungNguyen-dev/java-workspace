package tungnn.tutor.java.obsidian.vault;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.jackson.v3.JsonUtils;
import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.cli.ObsidianCommand;

public class VaultService {

    private static final JsonMapper MAPPER = JsonUtils.createJsonMapper();

    private final ObsidianCli cli;

    public VaultService(ObsidianCli cli) {
        this.cli = cli;
    }

    public Path root() {
        return Path.of(
                cli.execute(ObsidianCommand.of("vault").param("info", "path")).strip());
    }

    public String name() {
        return cli.execute(ObsidianCommand.of("vault").param("info", "name")).strip();
    }

    public List<String> files(String folder) {
        return cli.executeLines(ObsidianCommand.of("files").param("folder", folder));
    }

    public List<String> folders() {
        return cli.executeLines(ObsidianCommand.of("folders"));
    }

    public List<String> orphans() {
        return cli.executeLines(ObsidianCommand.of("orphans"));
    }

    public List<String> deadends() {
        return cli.executeLines(ObsidianCommand.of("deadends"));
    }

    public Map<String, Integer> tagCounts() {
        var json =
                cli.execute(ObsidianCommand.of("tags").param("format", "json").flag("counts"));
        return parseCounts(json);
    }

    public Map<String, List<String>> unresolved() {
        var json = cli.execute(
                ObsidianCommand.of("unresolved").param("format", "json").flag("verbose"));
        var result = new LinkedHashMap<String, List<String>>();
        var root = MAPPER.readTree(json);
        if (root.isObject()) {
            root.properties().forEach(e -> result.put(e.getKey(), strings(e.getValue())));
        } else {
            for (var item : root) {
                var target = firstText(item, "link", "target", "name", "path");
                if (target != null) {
                    result.put(target, strings(first(item, "sources", "files", "source")));
                }
            }
        }
        return result;
    }

    public List<String> search(String query, int limit) {
        var json = cli.execute(ObsidianCommand.of("search")
                .param("query", query)
                .param("limit", String.valueOf(limit))
                .param("format", "json"));
        return strings(MAPPER.readTree(json));
    }

    static Map<String, Integer> parseCounts(String json) {
        var result = new LinkedHashMap<String, Integer>();
        var root = MAPPER.readTree(json);
        if (root.isObject()) {
            root.properties().forEach(e -> result.put(e.getKey(), toInt(e.getValue())));
        } else {
            for (var item : root) {
                var name = firstText(item, "tag", "name");
                if (name != null) {
                    result.put(
                            name,
                            first(item, "count", "total") == null
                                    ? 0
                                    : first(item, "count", "total").asInt());
                }
            }
        }
        return result;
    }

    private static JsonNode first(JsonNode node, String... keys) {
        for (var key : keys) {
            if (node.has(key)) {
                return node.get(key);
            }
        }
        return null;
    }

    private static String firstText(JsonNode node, String... keys) {
        if (node.isString()) {
            return node.asString();
        }
        var found = first(node, keys);
        return found == null ? null : found.asString();
    }

    private static List<String> strings(JsonNode node) {
        var out = new ArrayList<String>();
        if (node == null || node.isNull()) {
            return out;
        }
        if (node.isArray()) {
            for (var item : node) {
                var text = firstText(item, "path", "file", "name");
                if (text != null) {
                    out.add(text);
                }
            }
        } else if (node.isString()) {
            // The CLI joins multiple sources into one ", "-separated string
            for (var part : node.asString().split("(?<=\\.md), ")) {
                if (!part.isBlank()) {
                    out.add(part.strip());
                }
            }
        }
        return out;
    }

    private static int toInt(JsonNode node) {
        if (node == null || node.isNull()) {
            return 0;
        }
        try {
            return Integer.parseInt(node.asString().strip());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
