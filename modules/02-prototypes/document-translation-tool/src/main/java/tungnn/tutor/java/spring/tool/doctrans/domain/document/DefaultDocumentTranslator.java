package tungnn.tutor.java.spring.tool.doctrans.domain.document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import tungnn.tutor.java.spring.tool.doctrans.domain.shared.TextUnitMapper;
import tungnn.tutor.java.spring.tool.doctrans.domain.shared.TextUnitMapping;
import tungnn.tutor.java.spring.tool.doctrans.domain.text.*;
import tungnn.tutor.java.spring.tool.doctrans.shared.LanguageCode;
import tungnn.tutor.java.spring.tool.doctrans.shared.TranslationError;

public class DefaultDocumentTranslator implements DocumentTranslator {

  protected final TextTranslator textTranslator;
  protected final TextUnitMapper textUnitMapper;

  protected DefaultDocumentTranslator(
      TextTranslator textTranslator, TextUnitMapper textUnitMapper) {

    this.textTranslator = Objects.requireNonNull(textTranslator, "textTranslator must not be null");

    this.textUnitMapper = Objects.requireNonNull(textUnitMapper, "textUnitMapper must not be null");
  }

  @Override
  public DocumentTranslationResult translate(Document document, LanguageCode targetLanguage) {

    try {
      validateInput(document, targetLanguage);

      // Phase 1: Collect and Translate
      var sequenceTranslations = collectAndTranslate(document, targetLanguage);

      // Phase 2: Apply Translation
      applyTranslations(document, sequenceTranslations);

      return new DocumentTranslationResult.Success();

    } catch (TranslationException e) {
      return new DocumentTranslationResult.Failure(e.error());

    } catch (Exception e) {
      return new DocumentTranslationResult.Failure(new TranslationError("", e.getMessage()));
    }
  }

  private List<SequenceTranslation> collectAndTranslate(
      Document document, LanguageCode targetLanguage) {

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      List<CompletableFuture<Optional<SequenceTranslation>>> futures =
          document.textNodeSequences().stream()
              .map(
                  sequence ->
                      CompletableFuture.supplyAsync(
                              () -> processSequence(sequence, targetLanguage), executor)
                          .exceptionally(
                              throwable -> {
                                return Optional.empty();
                              }))
              .toList();

      return futures.stream().map(CompletableFuture::join).flatMap(Optional::stream).toList();
    }
  }

  private Optional<SequenceTranslation> processSequence(
      TextBlockSequence sequence, LanguageCode targetLanguage) {

    var semanticUnits = collectSemanticUnits(sequence);

    if (semanticUnits.isEmpty()) {
      return Optional.empty();
    }

    var mapping = textUnitMapper.map(semanticUnits);

    var translations = translateTextUnits(mapping, targetLanguage);

    return Optional.of(new SequenceTranslation(sequence, mapping, translations));
  }

  private List<SemanticUnit> collectSemanticUnits(TextBlockSequence sequence) {

    return sequence.textBlocks().stream()
        .flatMap(textBlock -> textBlock.semanticUnits().stream())
        .toList();
  }

  private List<TextUnitTranslation> translateTextUnits(
      TextUnitMapping mapping, LanguageCode targetLanguage) {

    var request =
        new TextTranslationRequest.TextBatchTranslationRequest(mapping.textUnits(), targetLanguage);

    var result = textTranslator.translate(request);

    return switch (result) {
      case TextTranslationResult.Success success ->
          validateTranslations(mapping, success.translations(), targetLanguage);

      case TextTranslationResult.Failure failure -> throw new TranslationException(failure.error());
    };
  }

  private void applyTranslations(
      Document document, List<SequenceTranslation> sequenceTranslations) {

    for (var sequenceTranslation : sequenceTranslations) {
      applySequenceTranslation(sequenceTranslation);
    }
  }

  private List<TextUnitTranslation> validateTranslations(
      TextUnitMapping mapping,
      List<TextUnitTranslation> translations,
      LanguageCode targetLanguage) {

    if (translations == null) {
      throw new IllegalStateException("translations must not be null");
    }

    var expectedTextIds =
        mapping.textUnits().stream().map(TextUnit::textId).collect(Collectors.toSet());

    var actualTextIds =
        translations.stream().map(TextUnitTranslation::textId).collect(Collectors.toSet());

    if (actualTextIds.size() != translations.size()) {

      throw new IllegalStateException("Duplicate translation textId detected");
    }

    var missingTextIds = difference(expectedTextIds, actualTextIds);

    if (!missingTextIds.isEmpty()) {
      throw new IllegalStateException("Missing translations for textIds: " + missingTextIds);
    }

    var unexpectedTextIds = difference(actualTextIds, expectedTextIds);

    if (!unexpectedTextIds.isEmpty()) {
      throw new IllegalStateException("Unexpected translations for textIds: " + unexpectedTextIds);
    }

    var textUnitsById =
        mapping.textUnits().stream()
            .collect(Collectors.toUnmodifiableMap(TextUnit::textId, textUnit -> textUnit));

    for (var translation : translations) {
      if (translation.targetLanguage() != targetLanguage) {

        throw new IllegalStateException(
            "Unexpected target language for textId: " + translation.textId());
      }

      var textUnit = textUnitsById.get(translation.textId());

      if (!Objects.equals(textUnit.textContent(), translation.original())) {

        throw new IllegalStateException(
            "Original text mismatch for textId: " + translation.textId());
      }
    }

    return List.copyOf(translations);
  }

  private void applySequenceTranslation(SequenceTranslation sequenceTranslation) {

    var mapping = sequenceTranslation.mapping();
    var translations = sequenceTranslation.translations();
    var sequence = sequenceTranslation.sequence();

    var translationsByTextId =
        translations.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    TextUnitTranslation::textId, translation -> translation));

    var translatedUnitsByTextNodeId = new LinkedHashMap<String, List<SemanticUnitTranslation>>();

    for (var entry : mapping.semanticUnitsByTextId().entrySet()) {

      var textId = entry.getKey();
      var translation = translationsByTextId.get(textId);

      if (translation == null) {
        throw new IllegalStateException("Missing translation for textId: " + textId);
      }

      for (var semanticUnit : entry.getValue()) {

        var translatedUnit =
            new SemanticUnitTranslation(
                semanticUnit.textNodeId(),
                semanticUnit.index(),
                semanticUnit.semanticText(),
                translation.translation());

        translatedUnitsByTextNodeId
            .computeIfAbsent(semanticUnit.textNodeId(), ignored -> new ArrayList<>())
            .add(translatedUnit);
      }
    }

    applyToTextNodes(sequence, translatedUnitsByTextNodeId);
  }

  private void applyToTextNodes(
      TextBlockSequence sequence,
      Map<String, List<SemanticUnitTranslation>> translatedUnitsByTextNodeId) {

    for (var textBlock : sequence.textBlocks()) {

      var translatedUnits = translatedUnitsByTextNodeId.get(textBlock.textBlockId());

      if (translatedUnits == null) {
        continue;
      }

      var orderedUnits =
          translatedUnits.stream()
              .sorted(Comparator.comparingInt(SemanticUnitTranslation::index))
              .toList();

      validateNodeUnits(textBlock, orderedUnits);

      if (!textBlock.replaceContent(orderedUnits)) {
        throw new IllegalStateException(
            "Failed to replace content of textBlock: " + textBlock.textBlockId());
      }
    }
  }

  private void validateNodeUnits(TextBlock textBlock, List<SemanticUnitTranslation> units) {

    var expectedUnits = textBlock.semanticUnits();

    if (expectedUnits.size() != units.size()) {

      throw new IllegalStateException(
          "Semantic unit count mismatch "
              + "for textBlock: "
              + textBlock.textBlockId()
              + ". Expected: "
              + expectedUnits.size()
              + ", actual: "
              + units.size());
    }

    for (var index = 0; index < expectedUnits.size(); index++) {

      var expected = expectedUnits.get(index);

      var actual = units.get(index);

      if (expected.index() != actual.index()) {

        throw new IllegalStateException(
            "Semantic unit order mismatch "
                + "for textBlock: "
                + textBlock.textBlockId()
                + " at index: "
                + index);
      }

      if (!Objects.equals(expected.textNodeId(), actual.textNodeId())) {

        throw new IllegalStateException(
            "Semantic unit textNodeId mismatch " + "for textBlock: " + textBlock.textBlockId());
      }

      if (!Objects.equals(expected.semanticText(), actual.sourceText())) {

        throw new IllegalStateException(
            "Semantic unit source text mismatch "
                + "for textBlock: "
                + textBlock.textBlockId()
                + " at index: "
                + index);
      }
    }
  }

  private void validateInput(Document document, LanguageCode targetLanguage) {

    Objects.requireNonNull(document, "document must not be null");

    Objects.requireNonNull(targetLanguage, "targetLanguage must not be null");

    Objects.requireNonNull(
        document.textNodeSequences(), "document.textNodeSequences " + "must not be null");

    for (var sequence : document.textNodeSequences()) {

      Objects.requireNonNull(sequence, "textNodeSequence must not be null");

      Objects.requireNonNull(
          sequence.textBlocks(), "textNodeSequence.textNodes " + "must not be null");

      for (var textBlock : sequence.textBlocks()) {

        Objects.requireNonNull(textBlock, "textBlock must not be null");

        if (textBlock.textBlockId() == null || textBlock.textBlockId().isBlank()) {

          throw new IllegalArgumentException("textBlock.textNodeId " + "must not be blank");
        }

        Objects.requireNonNull(
            textBlock.semanticUnits(), "textBlock.semanticUnits " + "must not be null");
      }
    }
  }

  private Set<String> difference(Set<String> left, Set<String> right) {

    return left.stream()
        .filter(value -> !right.contains(value))
        .collect(Collectors.toUnmodifiableSet());
  }

  private record SequenceTranslation(
      TextBlockSequence sequence, TextUnitMapping mapping, List<TextUnitTranslation> translations) {

    private SequenceTranslation {
      Objects.requireNonNull(sequence, "sequence must not be null");
      Objects.requireNonNull(mapping, "mapping must not be null");
      translations =
          List.copyOf(Objects.requireNonNull(translations, "translations must not be null"));
    }
  }

  private static final class TranslationException extends RuntimeException {

    private final TranslationError error;

    private TranslationException(TranslationError error) {

      super(error == null ? null : error.message());

      this.error = Objects.requireNonNull(error, "error must not be null");
    }

    private TranslationError error() {
      return error;
    }
  }
}
