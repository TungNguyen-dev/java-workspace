package tungnn.tutor.java.starter.infrastructure.dictionary;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Dictionary {

  // Thread-safe indexes (single source of truth)
  private final ConcurrentMap<String, DictionaryItem> byId = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, DictionaryItem> byOriginal = new ConcurrentHashMap<>();

  private final DictionaryRepository repository;
  private final DictionaryTranslator translator;

  public Dictionary(DictionaryRepository repository, DictionaryTranslator translator) {
    this.repository = Objects.requireNonNull(repository);
    this.translator = Objects.requireNonNull(translator);

    loadDictionary();
  }

  public void loadDictionary() {
    byId.clear();
    byOriginal.clear();

    for (var item : repository.findAll()) {
      index(item);
    }
  }

  public void doTranslateMissing() {
    var missingItems =
        byId.values().stream()
            .filter(item -> item.translation() == null || item.translation().isBlank());

    translator.translate(missingItems);
  }

  public boolean containsOriginal(String original) {
    return byOriginal.containsKey(normalize(original));
  }

  public Optional<DictionaryItem> getItemByOriginal(String original) {
    return Optional.ofNullable(byOriginal.get(normalize(original)));
  }

  public Optional<DictionaryItem> getItemById(String id) {
    return Optional.ofNullable(byId.get(id));
  }

  public void addItem(DictionaryItem item) {
    Objects.requireNonNull(item, "item must not be null");
    Objects.requireNonNull(item.id(), "id must not be null");
    Objects.requireNonNull(item.original(), "original must not be null");

    String normalized = normalize(item.original());

    // Prevent duplicate original (atomic)
    var existing = byOriginal.putIfAbsent(normalized, item);
    if (existing != null) {
      throw new IllegalArgumentException("Duplicate original: " + item.original());
    }

    byId.put(item.id(), item);

    repository.save(item);
  }

  public DictionaryItem getOrCreate(String original) {
    Objects.requireNonNull(original, "original must not be null");

    var normalized = normalize(original);

    return byOriginal.computeIfAbsent(
        normalized,
        key -> {
          var newItem = new DictionaryItem(UUID.randomUUID().toString(), original, null);

          // Important: also update byId inside the same creation flow
          byId.put(newItem.id(), newItem);

          // Persist immediately (or you can batch later depending on design)
          repository.save(newItem);

          return newItem;
        });
  }

  private void index(DictionaryItem item) {
    byId.put(item.id(), item);
    byOriginal.put(normalize(item.original()), item);
  }

  private String normalize(String input) {
    if (input == null || input.isBlank()) {
      return "";
    }

    String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);
    normalized = normalized.replaceAll("\\s+", " ");
    normalized = normalized.trim();
    normalized = normalized.toLowerCase(Locale.ROOT);

    return normalized;
  }
}
