package tungnn.tutor.java.tool.doctrans.core.infra.dictionary;

import java.util.List;

public interface DictionaryRepository {

  List<DictionaryItem> findAll();

  void saveAll(List<DictionaryItem> items);

  void save(DictionaryItem item);
}
