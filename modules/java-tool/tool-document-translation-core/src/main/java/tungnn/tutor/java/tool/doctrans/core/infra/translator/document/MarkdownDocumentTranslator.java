package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.TranslationCache;

public class MarkdownDocumentTranslator extends AbstractDocumentTranslator<MarkdownElement.MarkdownDocument> {
    private static final TextTranslationBatchPolicy DEFAULT_BATCH_POLICY = new TextTranslationBatchPolicy(20, 3000);
    private final Map<MarkdownBlock, DocumentTextBlock> blockMapping = new IdentityHashMap<>();

    public MarkdownDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public MarkdownDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationBatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    @Override
    protected TextTranslationBatchPolicy defaultBatchPolicy() {
        return DEFAULT_BATCH_POLICY;
    }

    @Override
    protected MarkdownElement.MarkdownDocument openDocument(DocumentTranslationRequest.Single request) {
        try {
            blockMapping.clear();
            return MarkdownDocumentUtils.readDocument(request.documentSource().path());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Markdown document: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<DocumentSegment> collectSegments(MarkdownElement.MarkdownDocument document) {
        var segments = new ArrayList<DocumentSegment>();
        var current = new ArrayList<DocumentTextBlock>();
        for (var block : document.blocks()) {
            DocumentTextBlock textBlock;
            if (block instanceof MarkdownBlock.Heading heading) {
                // A heading opens a new segment: it stays together with the paragraphs under it.
                flush(segments, current);
                textBlock = new HeadingTextBlock(heading);
                current.add(textBlock);
                blockMapping.put(heading, textBlock);
            } else if (block instanceof MarkdownBlock.Paragraph paragraph) {
                textBlock = new ParagraphTextBlock(paragraph);
                current.add(textBlock);
                blockMapping.put(paragraph, textBlock);
            }
            // CodeBlock and other elements are preserved intact (source code is not translated)
        }
        flush(segments, current);
        return segments;
    }

    private static void flush(List<DocumentSegment> segments, List<DocumentTextBlock> current) {
        if (!current.isEmpty()) {
            segments.add(new DocumentSegment(current));
            current.clear();
        }
    }

    @Override
    protected void saveDocument(Path translationPath, MarkdownElement.MarkdownDocument document) {
        List<MarkdownBlock> translatedBlocks = new ArrayList<>();
        for (var block : document.blocks()) {
            var textBlock = blockMapping.get(block);
            if (textBlock != null) {
                if (block instanceof MarkdownBlock.Heading h) {
                    translatedBlocks.add(new MarkdownBlock.Heading(h.level(), textBlock.content()));
                } else if (block instanceof MarkdownBlock.Paragraph p) {
                    translatedBlocks.add(new MarkdownBlock.Paragraph(textBlock.content()));
                } else {
                    translatedBlocks.add(block);
                }
            } else {
                translatedBlocks.add(block);
            }
        }
        var translatedDoc = new MarkdownElement.MarkdownDocument(translatedBlocks);
        try {
            MarkdownDocumentUtils.writeDocument(translatedDoc, translationPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Markdown document: " + e.getMessage(), e);
        } finally {
            blockMapping.clear();
        }
    }

    // --- Implementations of DocumentTextBlock ---
    private static class HeadingTextBlock implements DocumentTextBlock {
        private final MarkdownBlock.Heading heading;
        private String content;

        public HeadingTextBlock(MarkdownBlock.Heading heading) {
            this.heading = heading;
            this.content = heading.text();
        }

        @Override
        public String content() {
            return content;
        }

        @Override
        public void setContent(String content) {
            this.content = content;
        }
    }

    private static class ParagraphTextBlock implements DocumentTextBlock {
        private final MarkdownBlock.Paragraph paragraph;
        private String content;

        public ParagraphTextBlock(MarkdownBlock.Paragraph paragraph) {
            this.paragraph = paragraph;
            this.content = paragraph.text();
        }

        @Override
        public String content() {
            return content;
        }

        @Override
        public void setContent(String content) {
            this.content = content;
        }
    }
}
