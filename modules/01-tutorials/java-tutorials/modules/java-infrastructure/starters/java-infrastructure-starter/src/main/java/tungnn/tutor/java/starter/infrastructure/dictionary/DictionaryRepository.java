package tungnn.tutor.java.starter.infrastructure.dictionary;

import java.util.List;

public interface DictionaryRepository {

  List<DictionaryItem> findAll();

  void saveAll(List<DictionaryItem> items);

  void save(DictionaryItem item);
}
