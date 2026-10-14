package tungnn.tutor.java.core.jls.components;

/**
 * Demonstrates the various kinds of class member declarations that can appear within a Java class.
 */
public class ClassMemberDeclarations {

  /**
   * A field (data member) declared inside the class. This represents an instance variable that
   * holds object state.
   */
  String field;

  /**
   * A method declaration inside the class. Methods define behaviors or operations that objects of
   * this class can perform.
   */
  void method() {
    // Method body (currently empty)
  }

  /**
   * A nested interface declared within the class. Such interfaces are implicitly static and can be
   * implemented by other classes.
   */
  interface NestedInterface {
    // Method declarations for the nested interface
  }

  /**
   * A nested class (also known as an inner class). It is a member class defined within another
   * class.
   */
  class NestedClass {
    // Members of the nested class
  }

  // A stray semicolon is syntactically valid but has no effect.
  ;
}
