void main() {
  // 1. Setup the Receiver
  Cook cook = new Cook();

  // 2. Create Commands (Orders)
  Command burgerOrder = new BurgerCommand(cook);
  Command shakeOrder = new ShakeCommand(cook);

  // 3. Setup the Invoker
  Waitress waitress = new Waitress();

  // 4. Trigger actions
  System.out.println("--- Customer orders a burger ---");
  waitress.setOrder(burgerOrder);
  waitress.orderUp();

  System.out.println("\n--- Customer orders a shake ---");
  waitress.setOrder(shakeOrder);
  waitress.orderUp();

  System.out.println("\n--- Customer changes their mind ---");
  waitress.oopsUndo();
}

// The Command Interface
public interface Command {
  void execute();

  void undo(); // Added to support the 'undoable operations' requirement
}

// The Receiver
public class Cook {
  public void makeBurger() {
    System.out.println("Cook: Grilling a juicy burger...");
  }

  public void makeShake() {
    System.out.println("Cook: Blending a chocolate shake...");
  }
}

// Concrete Commands
// Command to make a Burger
public class BurgerCommand implements Command {
  private Cook cook;

  public BurgerCommand(Cook cook) {
    this.cook = cook;
  }

  @Override
  public void execute() {
    cook.makeBurger();
  }

  @Override
  public void undo() {
    System.out.println("Waitress: Cancel the burger order!");
  }
}

// Command to make a Shake
public class ShakeCommand implements Command {
  private Cook cook;

  public ShakeCommand(Cook cook) {
    this.cook = cook;
  }

  @Override
  public void execute() {
    cook.makeShake();
  }

  @Override
  public void undo() {
    System.out.println("Waitress: Cancel the shake order!");
  }
}

// The Invoker
public class Waitress {
  private Command slot;
  private Stack<Command> history = new Stack<>(); // To support undo functionality

  public void setOrder(Command command) {
    this.slot = command;
  }

  public void orderUp() {
    slot.execute();
    history.push(slot);
  }

  public void oopsUndo() {
    if (!history.isEmpty()) {
      Command lastOrder = history.pop();
      lastOrder.undo();
    }
  }
}
