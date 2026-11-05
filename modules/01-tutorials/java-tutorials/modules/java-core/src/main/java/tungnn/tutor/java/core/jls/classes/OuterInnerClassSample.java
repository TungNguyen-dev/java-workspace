package tungnn.tutor.java.core.jls.classes;

public class OuterInnerClassSample {

  static void main() {
    var outer = new OuterInnerClassSample();

    // Instantiating a non-static inner class requires an enclosing instance of the outer class
    var inner = outer.new Inner();

    // Each nested inner class instance is implicitly bound to its specific enclosing instance
    var inner2 = inner.new Inner2();
    var inner3 = inner2.new Inner3();

    // Instantiating a subclass of a deeply nested inner class using a qualified super constructor
    var childInner = outer.new ChildInner(inner2);
  }

  class Inner {

    class Inner2 {

      class Inner3 {

        // Explicit Receiver Parameter syntax to explicitly declare the enclosing instance of Inner2
        public Inner3(Inner2 Inner2.this) {
          // Accessing the immediately enclosing instance (Inner2)
          System.out.println(Inner2.this);

          // Accessing the outer enclosing instance (Inner) via qualified 'this' syntax
          System.out.println(Inner.this);
        }
      }
    }
  }

  class ChildInner extends Inner.Inner2.Inner3 {

    // Qualified Super Constructor invocation passing the specific enclosing instance
    ChildInner(Inner.Inner2 inner2) {
      inner2.super();
    }
  }
}
