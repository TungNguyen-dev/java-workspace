package tungnn.tutor.java.core.jls.components;

// ================================================================
//  Inner Class Declarations and Static Members
// ================================================================

class HasStatic {
  static int j = 100;
}

class Outer {

  /*
   * INFO:
   * Interfaces declared within a class are implicitly static,
   * so they are never considered inner classes.
   */
  interface NeverInner {}

  static class NestedButNotInner {
    // Accessing a static member of Inner (if it were legal)
    int z = Inner.x;
  }

  class Inner extends HasStatic {

    static final int y = 4; // ✅ Allowed — constant expression
    static int x = 3; // ❌ Not allowed — static field in a non-static inner class

    /*
     * ❌ Illegal Declarations in Inner Classes
     * Inner classes cannot declare static members unless they are compile-time
     * constants.
     * Only static final fields initialized with constant expressions are permitted.
     */
    static {
      System.out.println("Hello from Outer.Inner");
    }

    static void hello() { // ❌ Not allowed — static method in a non-static inner class
      System.out.println("Hello from Outer.Inner.hello");
    }

    /*
     * Nested static classes are permitted inside an inner class declaration,
     * but they are not considered “inner” themselves (no enclosing instance).
     */
    static class VeryNestedButNotInner extends NestedButNotInner {}
  }
}

// ================================================================
// Inner Class Declarations
// ================================================================

class Outer2 {
  int i = 100;

  static void classMethod() {
    final int l = 200;

    class LocalInStaticContext {
      /*
       * NOTE:
       * A local class defined in a static context cannot access
       * instance members of the enclosing class (e.g., `i`).
       */
      // int k = i; // ❌ Compile-time error
      int m = l; // ✅ Allowed — can access effectively final local variables
    }
  }

  void foo() {
    class Local { // ✅ A local (non-static) inner class
      int j = i; // Can access instance members of the enclosing class
    }
  }
}

// ================================================================
// Enclosing Instance Access
// ================================================================

class WithDeepNesting {

  boolean toBe;

  WithDeepNesting(boolean b) {
    toBe = b;
  }

  class Nested {

    boolean theQuestion;

    class DeeplyNested {

      DeeplyNested() {
        /*
         * Deeply nested inner classes can still access members of
         * all enclosing instances through the chain of enclosing objects.
         */
        theQuestion = toBe || !toBe;
      }
    }
  }
}

// ================================================================
// Subclass of an Inner Class
// ================================================================

class SO {
  int secret = 5;

  class S {
    int getSecret() {
      return secret;
    }

    void setSecret(int s) {
      secret = s;
    }
  }
}

class C extends SO.S {

  /*
   * ================================================================
   * Qualified Superclass Constructor Invocation
   * ================================================================
   *
   * Why `so.super()` is required:
   * - The superclass `SO.S` is an inner class that requires an instance
   * of `SO` as its enclosing instance.
   * - Class `C` is NOT an inner class of `SO`, so it does not have
   * an implicit enclosing `SO` instance.
   * - Therefore, when invoking the constructor of `S`, the subclass
   * must explicitly qualify the superclass constructor with
   * an instance of `SO`, e.g., `so.super()`.
   */
  C(SO so) {
    so.super();
  }
}
