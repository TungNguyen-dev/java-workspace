void main() {
  CaffeineBeverage tea = new Tea();
  CaffeineBeverage coffee = new Coffee();

  System.out.println("--- Making Tea ---");
  tea.prepareRecipe();

  System.out.println("\n--- Making Coffee ---");
  coffee.prepareRecipe();
}

// The Abstract Class (The Skeleton)
abstract class CaffeineBeverage {

  // The Template Method: Final so subclasses can't change the recipe
  final void prepareRecipe() {
    boilWater();
    brew();
    pourInCup();
    if (customerWantsCondiments()) { // Using a Hook
      addCondiments();
    }
  }

  // Abstract methods to be implemented by subclasses
  abstract void brew();

  abstract void addCondiments();

  // Concrete operations shared by all
  void boilWater() {
    System.out.println("Boiling water...");
  }

  void pourInCup() {
    System.out.println("Pouring into cup...");
  }

  // A Hook: Subclasses can override this, but they don't have to
  boolean customerWantsCondiments() {
    return true;
  }
}

// Concrete Classes (The Implementation)
class Tea extends CaffeineBeverage {
  @Override
  void brew() {
    System.out.println("Steeping the tea...");
  }

  @Override
  void addCondiments() {
    System.out.println("Adding Lemon.");
  }
}

class Coffee extends CaffeineBeverage {
  @Override
  void brew() {
    System.out.println("Dripping Coffee through filter...");
  }

  @Override
  void addCondiments() {
    System.out.println("Adding Sugar and Milk.");
  }

  // Overriding the hook to provide custom logic
  @Override
  boolean customerWantsCondiments() {
    return false; // This customer wants black coffee
  }
}
