package tungnn.tutor.java.tool.doctrans.core.document.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement;
import tungnn.tutor.java.tool.doctrans.core.cache.TranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;

public class MarkdownDocumentTranslator extends AbstractDocumentTranslator<MarkdownElement.MarkdownDocument> {
    private static final TextTranslationRequest.BatchPolicy DEFAULT_BATCH_POLICY =
            new TextTranslationRequest.BatchPolicy(20, 3000);
    private final Map<MarkdownBlock, DocumentText.Block> blockMapping = new IdentityHashMap<>();

    public MarkdownDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public MarkdownDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationRequest.BatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    private static void flush(List<DocumentText.Segment> segments, List<DocumentText.Block> current) {
        if (!current.isEmpty()) {
            segments.add(new DocumentText.Segment(current));
            current.clear();
        }
    }

    @Override
    protected TextTranslationRequest.BatchPolicy defaultBatchPolicy() {
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
    protected List<DocumentText.Segment> collectSegments(MarkdownElement.MarkdownDocument document) {
        var segments = new ArrayList<DocumentText.Segment>();
        var current = new ArrayList<DocumentText.Block>();
        for (var block : document.blocks()) {
            DocumentText.Block textBlock;
            if (block instanceof MarkdownBlock.Heading heading) {
                // A heading opens a new segment: it stays together with the paragraphs under it.
                flush(segments, current);
                textBlock = new HeadingBlock(heading);
                current.add(textBlock);
                blockMapping.put(heading, textBlock);
            } else if (block instanceof MarkdownBlock.Paragraph paragraph) {
                textBlock = new ParagraphBlock(paragraph);
                current.add(textBlock);
                blockMapping.put(paragraph, textBlock);
            }
            // CodeBlock and other elements are preserved intact (source code is not translated)
        }
        flush(segments, current);
        return segments;
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

    // --- Implementations of DocumentText.Block ---
    private static class HeadingBlock implements DocumentText.Block {
        private final MarkdownBlock.Heading heading;
        private String content;

        public HeadingBlock(MarkdownBlock.Heading heading) {
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

    private static class ParagraphBlock implements DocumentText.Block {
        private final MarkdownBlock.Paragraph paragraph;
        private String content;

        public ParagraphBlock(MarkdownBlock.Paragraph paragraph) {
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
