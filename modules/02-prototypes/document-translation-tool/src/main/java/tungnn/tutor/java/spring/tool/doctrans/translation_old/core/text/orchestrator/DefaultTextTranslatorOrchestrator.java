package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import tungnn.tutor.java.core.lib.collection.CollectionChunks;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.TextTranslationRequest;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.TextTranslationResponse;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.TextTranslator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.exception.TranslationException;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public class DefaultTextTranslatorOrchestrator implements TextTranslatorOrchestrator {

  private final TextTranslator textTranslator;
  private final int chunkSize;
  private final Executor executor;
  private final Semaphore semaphore;

  public DefaultTextTranslatorOrchestrator(
      TextTranslator textTranslator, int chunkSize, Executor executor, int maxConcurrency) {
    this.textTranslator = textTranslator;
    this.chunkSize = chunkSize;
    this.executor = executor;
    this.semaphore = new Semaphore(maxConcurrency);
  }

  @Override
  public TranslationResult translate(
      List<TextReference> textReferences, TranslationContext context) {

    if (textReferences == null || textReferences.isEmpty()) {
      return TranslationResult.empty();
    }

    var idToReferences = new HashMap<String, List<TextReference>>();
    var textToId = new HashMap<String, String>();
    var units = new ArrayList<TextTranslationRequest.TextUnit>();

    for (var reference : textReferences) {
      var text = reference.getText();
      var id =
          textToId.computeIfAbsent(
              text,
              t -> {
                var newId = generateId();
                units.add(new TextTranslationRequest.TextUnit(newId, t));
                return newId;
              });
      idToReferences.computeIfAbsent(id, k -> new ArrayList<>()).add(reference);
    }

    var futures =
        CollectionChunks.chunkStream(units, chunkSize)
            .map(chunk -> translateChunkAsync(chunk, context))
            .toList();

    CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

    var translations = new HashMap<String, String>();
    var failures = new ArrayList<Throwable>();
    for (var future : futures) {
      var result = future.join();
      if (result.isSuccess()) {
        translations.putAll(result.translations());
      } else {
        failures.add(result.error());
      }
    }

    if (!failures.isEmpty()) {
      var ex = new TranslationException(failures.size() + " chunk(s) failed to translate");
      failures.forEach(ex::addSuppressed);
      throw ex;
    }

    return new TranslationResult(buildEntries(idToReferences, translations));
  }

  private CompletableFuture<ChunkTranslateResult> translateChunkAsync(
      List<TextTranslationRequest.TextUnit> chunk, TranslationContext context) {
    return CompletableFuture.supplyAsync(() -> translateChunk(chunk, context), executor);
  }

  private ChunkTranslateResult translateChunk(
      List<TextTranslationRequest.TextUnit> chunk, TranslationContext context) {

    boolean acquired = false;
    try {
      semaphore.acquire();
      acquired = true;

      var request = new TextTranslationRequest(chunk, context.targetLanguage());
      var response = textTranslator.translate(request);
      return ChunkTranslateResult.success(extractTranslations(response));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ChunkTranslateResult.fail(e);
    } catch (Exception e) {
      return ChunkTranslateResult.fail(e);
    } finally {
      if (acquired) {
        semaphore.release();
      }
    }
  }

  private Map<String, String> extractTranslations(TextTranslationResponse response) {
    var translations = new HashMap<String, String>();
    for (var translated : response.translations()) {
      translations.put(translated.textId(), translated.translatedText());
    }
    return translations;
  }

  private List<TranslationResult.Entry> buildEntries(
      Map<String, List<TextReference>> idToReferences, Map<String, String> translations) {
    var entries = new ArrayList<TranslationResult.Entry>();
    for (var entry : translations.entrySet()) {
      var references = idToReferences.get(entry.getKey());
      if (references != null) {
        for (var reference : references) {
          entries.add(new TranslationResult.Entry(reference, entry.getValue()));
        }
      }
    }
    return entries;
  }

  private String generateId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  private record ChunkTranslateResult(Map<String, String> translations, Throwable error) {

    static ChunkTranslateResult success(Map<String, String> translations) {
      return new ChunkTranslateResult(Map.copyOf(translations), null);
    }

    static ChunkTranslateResult fail(Throwable error) {
      return new ChunkTranslateResult(Map.of(), error);
    }

    boolean isSuccess() {
      return error == null;
    }
  }
}
