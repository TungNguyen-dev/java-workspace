package tungnn.tutor.java.starter.infrastructure.dictionary;

import java.util.Objects;

public record DictionaryItem(String id, String original, String translation) {

  public DictionaryItem {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(original, "original must not be null");
  }

  public DictionaryItem withTranslation(String newTranslation) {
    return new DictionaryItem(this.id, this.original, newTranslation);
  }
}
