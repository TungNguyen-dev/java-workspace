package tungnn.tutor.java.tool.hub.plugin.medmd.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownRefiner {

  private MarkdownRefiner() {}

  public static RefineResult refine(String markdown, List<? extends RefineRule> rules) {
    Objects.requireNonNull(markdown, "markdown must not be null");
    Objects.requireNonNull(rules, "rules must not be null");

    MarkdownDocument document = MarkdownDocumentUtils.parse(markdown);
    RefineContext context = new RefineContext();
    List<RuleOutcome> outcomes = new ArrayList<>(rules.size());

    String currentMarkdown = document.toMarkdown();
    for (RefineRule rule : rules) {
      Objects.requireNonNull(rule, "rule must not be null");
      MarkdownDocument next =
          Objects.requireNonNull(
              rule.apply(document, context), () -> "Rule '" + rule.id() + "' trả về null");
      String nextMarkdown = next.toMarkdown();
      boolean changed = !currentMarkdown.equals(nextMarkdown);
      outcomes.add(new RuleOutcome(rule.id(), rule.name(), changed, context.drainNotes()));
      document = next;
      currentMarkdown = nextMarkdown;
    }

    return new RefineResult(withFinalNewline(currentMarkdown), outcomes);
  }

  private static String withFinalNewline(String markdown) {
    return markdown.isEmpty() || markdown.endsWith("\n") ? markdown : markdown + "\n";
  }

  public record RuleOutcome(String ruleId, String ruleName, boolean changed, List<String> notes) {
    public RuleOutcome {
      notes = notes == null ? List.of() : List.copyOf(notes);
    }
  }

  public record RefineResult(String markdown, List<RuleOutcome> outcomes) {
    public RefineResult {
      outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }

    public long changedCount() {
      return outcomes.stream().filter(RuleOutcome::changed).count();
    }
  }
}
