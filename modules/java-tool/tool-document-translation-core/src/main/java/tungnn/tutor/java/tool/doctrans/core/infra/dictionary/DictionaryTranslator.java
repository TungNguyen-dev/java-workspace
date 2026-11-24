package tungnn.tutor.java.tool.doctrans.core.infra.dictionary;

import java.util.List;
import java.util.stream.Stream;

public interface DictionaryTranslator {

    void translate(List<DictionaryItem> dictionaryItems);

    void translate(Stream<DictionaryItem> dictionaryItems);
}
