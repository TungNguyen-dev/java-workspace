package tungnn.tutor.java.tool.mdrefine.core;

import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownPipeline {

  private final List<Step> steps = new ArrayList<>();

  public MarkdownPipeline step(String name, MarkdownAction action) {
    steps.add(new Step(name, action));
    return this;
  }

  public MarkdownDocument run(MarkdownDocument source) {
    var doc = source;
    int i = 1;
    for (var step : steps) {
      int before = doc.blocks().size();
      doc = step.action().apply(doc);
      System.out.printf(
          "[%d/%d] %-45s blocks: %d -> %d%n",
          i++, steps.size(), step.name(), before, doc.blocks().size());
    }
    return doc;
  }

  private record Step(String name, MarkdownAction action) {}
}
