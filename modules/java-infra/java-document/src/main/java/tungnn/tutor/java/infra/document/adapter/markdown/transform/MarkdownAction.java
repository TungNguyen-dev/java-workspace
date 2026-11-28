package tungnn.tutor.java.infra.document.adapter.markdown.transform;

import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

@FunctionalInterface
public interface MarkdownAction {
    MarkdownDocument apply(MarkdownDocument doc);
}
