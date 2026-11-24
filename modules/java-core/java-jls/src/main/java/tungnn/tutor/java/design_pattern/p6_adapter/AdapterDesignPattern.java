// The client only accepts the Duck type
static void testDuck(DuckTargetInterface duck) {
  duck.quack();
  duck.fly();
}

void main(String[] args) {
  // Create a real turkey
  WildTurkeyAdaptee turkey = new WildTurkeyAdaptee();

  // Wrap the turkey in an adapter so it looks like a duck
  DuckTargetInterface turkeyAdapter = new TurkeyAdapter(turkey);

  System.out.println("The Turkey says...");
  turkey.gobble();
  turkey.fly();

  System.out.println("\nThe TurkeyAdapter says (as a Duck):");
  testDuck(turkeyAdapter);
}

// Target Interface
public interface DuckTargetInterface {
  void quack();

  void fly();
}

// Adapter Class
public static class TurkeyAdapter implements DuckTargetInterface {
  private final WildTurkeyAdaptee turkey;

  public TurkeyAdapter(WildTurkeyAdaptee turkey) {
    this.turkey = turkey;
  }

  @Override
  public void quack() {
    // Translate the quack() request into gobble()
    turkey.gobble();
  }

  @Override
  public void fly() {
    // A turkey flies a shorter distance than a duck, so it flies 5 times to compensate
    for (int i = 0; i < 5; i++) {
      turkey.fly();
    }
  }
}

// Adaptee Class
public static class WildTurkeyAdaptee {
  public void gobble() {
    System.out.println("Gobble gobble");
  }

  public void fly() {
    System.out.println("I'm flying a short distance");
  }
}
