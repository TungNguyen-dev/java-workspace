package tungnn.tutor.java.jol;

import org.openjdk.jol.info.ClassLayout;

public class DemoJOL {

  static void main() {
    // Example 1: Inspect a simple object
    Object obj = new Object();
    System.out.println("Object layout:");
    System.out.println(ClassLayout.parseInstance(obj).toPrintable());

    // Example 2: Inspect a custom class
    Sample sample = new Sample();
    System.out.println("Sample class layout:");
    System.out.println(ClassLayout.parseInstance(sample).toPrintable());

    // Example 3: Inspect an array
    int[] array = new int[5];
    System.out.println("Array layout:");
    System.out.println(ClassLayout.parseInstance(array).toPrintable());
  }

  static class Sample {
    int id;
    long timestamp;
    boolean active;
  }
}
