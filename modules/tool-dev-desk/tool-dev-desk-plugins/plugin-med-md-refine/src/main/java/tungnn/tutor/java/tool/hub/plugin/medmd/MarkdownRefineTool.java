package tungnn.tutor.java.tool.hub.plugin.medmd;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.pf4j.Extension;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;
import tungnn.tutor.java.tool.mdrefine.core.MarkdownAction;
import tungnn.tutor.java.tool.mdrefine.core.MarkdownActions;
import tungnn.tutor.java.tool.mdrefine.core.MarkdownRefiner;

@Extension
public class MarkdownRefineTool implements ToolExtension {

  static final String RULE_PARAM_PREFIX = "rule.";
  static final List<RefineRule> DEFAULT_RULES =
      List.of(
          new RefineRule(
              "normalize",
              "Normalize text",
              true,
              MarkdownActions.normalize(MarkdownRefiner.defaultMedical())));
  private final List<RefineRule> rules;

  public MarkdownRefineTool() {
    this(DEFAULT_RULES);
  }

  MarkdownRefineTool(List<RefineRule> rules) {
    this.rules = List.copyOf(rules);
  }

  static RefineResult refine(String markdown, List<RefineRule> rules) {
    MarkdownDocument document = MarkdownDocumentUtils.parse(markdown);
    List<RuleOutcome> outcomes = new ArrayList<>(rules.size());
    String current = document.toMarkdown();
    for (RefineRule rule : rules) {
      MarkdownDocument next = rule.action().apply(document);
      String nextMarkdown = next.toMarkdown();
      outcomes.add(new RuleOutcome(rule.id(), rule.name(), !current.equals(nextMarkdown)));
      document = next;
      current = nextMarkdown;
    }
    String withNewline = current.isEmpty() || current.endsWith("\n") ? current : current + "\n";
    return new RefineResult(withNewline, outcomes);
  }

  private static Source readSource(Map<String, Object> in) throws ToolException {
    if (in.get("file") instanceof ToolFile file) {
      String filename = file.filename() != null ? file.filename() : "document.md";
      String lower = filename.toLowerCase(Locale.ROOT);
      if (!(lower.endsWith(".md") || lower.endsWith(".markdown") || lower.endsWith(".txt"))) {
        throw new ToolException(
            "File '" + filename + "' is not Markdown (.md, .markdown, .txt)");
      }
      return new Source(baseName(filename), new String(file.content(), StandardCharsets.UTF_8));
    }
    if (in.get("markdown") instanceof CharSequence seq && !seq.toString().isBlank()) {
      return new Source("document", seq.toString());
    }
    throw new ToolException("Upload an .md file or paste Markdown content");
  }

  private static String baseName(String filename) {
    int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    String name = filename.substring(slash + 1);
    int dot = name.lastIndexOf('.');
    return dot > 0 ? name.substring(0, dot) : name;
  }

  @Override
  public ToolDescriptor descriptor() {
    return new ToolDescriptor(
        "med-md-refine",
        "Markdown Refine",
        "Refine Markdown file content with the rule set",
        "document",
        ToolLayout.FILE);
  }

  @Override
  public List<ParamSpec> params() {
    List<ParamSpec> params = new ArrayList<>();
    params.add(
        new ParamSpec("file", "file", false, "Markdown file (.md) — leave empty if pasting content"));
    params.add(new ParamSpec("markdown", "text", false, "Or paste Markdown content"));
    params.add(new ParamSpec("output", "enum:preview|download", true, "Result", "preview"));
    for (RefineRule rule : rules) {
      params.add(
          new ParamSpec(
              RULE_PARAM_PREFIX + rule.id(),
              "boolean",
              false,
              "Rule: " + rule.name(),
              String.valueOf(rule.enabledByDefault())));
    }
    return params;
  }

  @Override
  public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
    Source source = readSource(in);
    List<RefineRule> enabled =
        rules.stream()
            .filter(r -> Boolean.TRUE.equals(in.get(RULE_PARAM_PREFIX + r.id())))
            .toList();

    RefineResult result;
    try {
      result = refine(source.markdown(), enabled);
    } catch (RuntimeException e) {
      throw new ToolException("Refine failed: " + e.getMessage());
    }

    if (ctx != null && ctx.logger() != null) {
      ctx.logger()
          .info(
              "med-md-refine {}: {} rules, {} changes",
              source.name(),
              enabled.size(),
              result.changedCount());
    }

    String message = summary(enabled.size(), result);
    if ("download".equals(in.get("output"))) {
      return new ToolResult(
          true,
          new ToolFile(
              source.name() + ".refined.md",
              "text/markdown; charset=UTF-8",
              result.markdown().getBytes(StandardCharsets.UTF_8)),
          message);
    }
    return new ToolResult(true, result.markdown(), message);
  }

  private String summary(int enabledCount, RefineResult result) {
    if (rules.isEmpty()) {
      return "No rules defined (add them to DEFAULT_RULES) — the result only went through MarkdownDocumentUtils parse/render.";
    }
    if (enabledCount == 0) {
      return "No rule enabled — the result only went through MarkdownDocumentUtils parse/render.";
    }
    StringBuilder sb =
        new StringBuilder("Applied ")
            .append(enabledCount)
            .append(" rule, ")
            .append(result.changedCount())
            .append(" rules with changes: ");
    List<String> parts = new ArrayList<>();
    for (RuleOutcome o : result.outcomes()) {
      parts.add(o.ruleName() + (o.changed() ? " ✔" : " (unchanged)"));
    }
    return sb.append(String.join(" | ", parts)).toString();
  }

  record RefineRule(String id, String name, boolean enabledByDefault, MarkdownAction action) {}

  record RuleOutcome(String ruleId, String ruleName, boolean changed) {}

  record RefineResult(String markdown, List<RuleOutcome> outcomes) {
    long changedCount() {
      return outcomes.stream().filter(RuleOutcome::changed).count();
    }
  }

  private record Source(String name, String markdown) {}
}
