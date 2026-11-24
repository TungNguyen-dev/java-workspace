package tungnn.tutor.java.obsidian.report;

import java.util.List;
import java.util.Map;

public record LinkHealthReport(Map<String, List<String>> unresolved, List<String> orphans, List<String> deadends) {}
