package tungnn.tutor.java.core.lib.collection;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class CollectionChunks {

  private CollectionChunks() {}

  public static <E> List<List<E>> chunk(Collection<E> collection, int chunkSize) {
    validate(collection, chunkSize);

    List<List<E>> result = new ArrayList<>();
    List<E> current = new ArrayList<>(chunkSize);

    for (E element : collection) {
      current.add(element);
      if (current.size() == chunkSize) {
        result.add(current);
        current = new ArrayList<>(chunkSize);
      }
    }

    if (!current.isEmpty()) {
      result.add(current);
    }

    return result;
  }

  public static <E> Stream<List<E>> chunkStream(Collection<E> collection, int chunkSize) {
    validate(collection, chunkSize);
    return chunkStream(collection.stream(), chunkSize);
  }

  public static <E> Stream<List<E>> chunkStream(Stream<E> stream, int chunkSize) {
    if (stream == null) {
      throw new IllegalArgumentException("Stream must not be null");
    }
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("Chunk size must be greater than 0");
    }

    Iterator<E> iterator = stream.iterator();

    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            new Iterator<>() {
              @Override
              public boolean hasNext() {
                return iterator.hasNext();
              }

              @Override
              public List<E> next() {
                List<E> chunk = new ArrayList<>(chunkSize);
                int count = 0;

                while (iterator.hasNext() && count < chunkSize) {
                  chunk.add(iterator.next());
                  count++;
                }

                return chunk;
              }
            },
            Spliterator.ORDERED),
        false);
  }

  private static <E> void validate(Collection<E> collection, int chunkSize) {
    if (collection == null) {
      throw new IllegalArgumentException("Collection must not be null");
    }
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("Chunk size must be greater than 0");
    }
  }
}
