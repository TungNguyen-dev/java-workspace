package tungnn.tutor.java.tool.hub.plugin.medmd.rule;

import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public interface RefineRule {
  String id();

  String name();

  default boolean enabledByDefault() {
    return true;
  }

  MarkdownDocument apply(MarkdownDocument document, RefineContext context);
}
