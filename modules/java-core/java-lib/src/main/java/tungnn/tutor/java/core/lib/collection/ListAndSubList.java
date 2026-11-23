package tungnn.tutor.java.core.lib.collection;

import java.util.ArrayList;
import java.util.List;

public class ListAndSubList {

  public static void insertListUsingSubList() {
    List<Integer> mainList = new ArrayList<>(List.of(1, 2, 5, 6));
    List<Integer> toInsert = List.of(3, 4);

    // Insert at index 2
    mainList.subList(2, 2).addAll(toInsert);

    System.out.println(mainList); // [1, 2, 3, 4, 5, 6]
  }

  public static void demonstrateBackedView() {
    List<String> list = new ArrayList<>(List.of("A", "B", "C", "D"));
    List<String> subList = list.subList(1, 3);

    subList.set(0, "X");

    System.out.println(list); // [A, X, C, D]
  }

  public static void efficientBatchInsertion() {
    List<Integer> list = new ArrayList<>(List.of(1, 2, 6));
    List<Integer> batch = List.of(3, 4, 5);

    list.subList(2, 2).addAll(batch);

    System.out.println(list); // [1, 2, 3, 4, 5, 6]
  }

  public static void removeRangeUsingSubList() {
    List<String> list = new ArrayList<>(List.of("A", "B", "C", "D", "E"));

    list.subList(1, 4).clear();

    System.out.println(list); // [A, E]
  }
}
