package tungnn.tutor.java.tool.mdrefine.core;

import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

@FunctionalInterface
public interface MarkdownAction {
    MarkdownDocument apply(MarkdownDocument doc);
}
