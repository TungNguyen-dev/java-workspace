package tungnn.tutor.java.tool.meddoc.core.glossary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;

public final class GlossaryExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlossaryExtractor.class);
    private static final String PROMPT_RESOURCE = "prompts/glossary-tcm-terms.md";
    private static final String LANGUAGE_PLACEHOLDER = "{targetLanguage}";
    private static final int PRESCRIPTION_LEVEL = 3;
    private static final int BATCH_SIZE = 80;
    private static final Pattern HERB = Pattern.compile(
            "(\\p{IsHan}{1,6})\\s*(?:\\d+(?:\\.\\d+)?|[一二三四五六七八九十半]+)\\s*(?:克|g|G|钱|两|分|枚|片)");
    private static final Pattern BOOK_BRACKETS = Pattern.compile("[《》]");

    public record Result(Glossary glossary, int candidates, int failedBatches) {}

    private final MarkdownChat chat;
    private final String promptTemplate;

    public GlossaryExtractor(MarkdownChat chat) {
        this.chat = Objects.requireNonNull(chat, "chat must not be null");
        this.promptTemplate = ResourceUtils.getResourceAsString(PROMPT_RESOURCE);
    }

    public Result extract(MarkdownDocument source, LanguageCode target) {
        var candidates = candidates(source);
        var prompt = promptTemplate.replace(LANGUAGE_PLACEHOLDER, target.languageName());
        var terms = new LinkedHashMap<String, String>();
        int failed = 0;
        for (int from = 0; from < candidates.size(); from += BATCH_SIZE) {
            var batch = candidates.subList(from, Math.min(candidates.size(), from + BATCH_SIZE));
            try {
                terms.putAll(Glossary.parse(chat.chat(prompt, String.join("\n", batch))).terms());
            } catch (RuntimeException e) {
                failed++;
                LOGGER.warn("Glossary batch of {} terms failed: {}", batch.size(), e.getMessage());
            }
        }
        return new Result(new Glossary(terms), candidates.size(), failed);
    }

    public static List<String> candidates(MarkdownDocument source) {
        Set<String> found = new LinkedHashSet<>();
        for (var block : source.blocks()) {
            if (block instanceof Heading h && h.level() == PRESCRIPTION_LEVEL) {
                var name = BOOK_BRACKETS.matcher(h.text()).replaceAll("").strip();
                if (!name.isEmpty()) {
                    found.add(name);
                }
            } else if (block instanceof Paragraph p) {
                var matcher = HERB.matcher(p.text());
                while (matcher.find()) {
                    found.add(matcher.group(1));
                }
            }
        }
        return new ArrayList<>(found);
    }
}
