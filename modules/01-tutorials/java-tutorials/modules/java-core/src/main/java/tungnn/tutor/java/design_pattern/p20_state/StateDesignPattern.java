void main() {
  // Initialize machine with 5 gumballs
  GumballMachine gumballMachine = new GumballMachine(5);

  System.out.println("--- Gumball Machine Simulation ---");

  // Test 1: Standard operation
  System.out.println("\n[Test 1: Standard Turn]");
  gumballMachine.insertQuarter();
  gumballMachine.turnCrank();

  // Test 2: Ejecting a quarter
  System.out.println("\n[Test 2: Ejecting]");
  gumballMachine.insertQuarter();
  gumballMachine.ejectQuarter();

  // Test 3: Attempting to turn without a quarter
  System.out.println("\n[Test 3: Empty Turn]");
  gumballMachine.turnCrank();

  // Test 4: Stress testing the state transitions
  System.out.println("\n[Test 4: Multi-Turn Sequence]");
  gumballMachine.insertQuarter();
  gumballMachine.turnCrank();
  gumballMachine.insertQuarter();
  gumballMachine.turnCrank();

  // This final turn might trigger the WinnerState if you're lucky!
  gumballMachine.insertQuarter();
  gumballMachine.turnCrank();

  System.out.println("\n--- Remaining Inventory: " + gumballMachine.getCount() + " ---");
}

// The State Interface
public interface State {
  void insertQuarter();

  void ejectQuarter();

  void turnCrank();

  void dispense();
}

// The Context (GumballMachine)
public class GumballMachine {
  // Concrete state objects
  State soldOutState;
  State noQuarterState;
  State hasQuarterState;
  State soldState;
  State winnerState; // Added for the "1 in 10" game

  State state; // Current state
  int count = 0; // Number of gumballs

  public GumballMachine(int numberGumballs) {
    soldOutState = new SoldOutState(this);
    noQuarterState = new NoQuarterState(this);
    hasQuarterState = new HasQuarterState(this);
    soldState = new SoldState(this);
    winnerState = new WinnerState(this);

    this.count = numberGumballs;
    if (numberGumballs > 0) {
      state = noQuarterState;
    } else {
      state = soldOutState;
    }
  }

  // Actions are delegated to the current state object
  public void insertQuarter() {
    state.insertQuarter();
  }

  public void ejectQuarter() {
    state.ejectQuarter();
  }

  public void turnCrank() {
    state.turnCrank();
    state.dispense();
  }

  // Helper methods for states to trigger transitions
  void setState(State state) {
    this.state = state;
  }

  void releaseBall() {
    System.out.println("A gumball comes rolling out the slot...");
    if (count != 0) {
      count--;
    }
  }

  // Getters for states and count...
  public State getHasQuarterState() {
    return hasQuarterState;
  }

  public State getNoQuarterState() {
    return noQuarterState;
  }

  public State getSoldState() {
    return soldState;
  }

  public State getSoldOutState() {
    return soldOutState;
  }

  public State getWinnerState() {
    return winnerState;
  }

  public int getCount() {
    return count;
  }
}

// Concrete State Implementation
public class SoldOutState implements State {
  GumballMachine gumballMachine;

  public SoldOutState(GumballMachine gumballMachine) {
    this.gumballMachine = gumballMachine;
  }

  public void insertQuarter() {
    System.out.println("You can't insert a quarter, the machine is sold out");
  }

  public void ejectQuarter() {
    System.out.println("You can't eject, you haven't inserted a quarter yet");
  }

  public void turnCrank() {
    System.out.println("You turned, but there are no gumballs");
  }

  public void dispense() {
    System.out.println("No gumball dispensed");
  }
}

public class NoQuarterState implements State {
  GumballMachine gumballMachine;

  public NoQuarterState(GumballMachine gumballMachine) {
    this.gumballMachine = gumballMachine;
  }

  public void insertQuarter() {
    System.out.println("You inserted a quarter");
    gumballMachine.setState(gumballMachine.getHasQuarterState());
  }

  public void ejectQuarter() {
    System.out.println("You haven't inserted a quarter");
  }

  public void turnCrank() {
    System.out.println("You turned, but there's no quarter");
  }

  public void dispense() {
    System.out.println("You need to pay first");
  }
}

public class SoldState implements State {
  GumballMachine gumballMachine;

  public SoldState(GumballMachine gumballMachine) {
    this.gumballMachine = gumballMachine;
  }

  public void insertQuarter() {
    System.out.println("Please wait, we're already giving you a gumball");
  }

  public void ejectQuarter() {
    System.out.println("Sorry, you already turned the crank");
  }

  public void turnCrank() {
    System.out.println("Turning twice doesn't get you another gumball!");
  }

  public void dispense() {
    gumballMachine.releaseBall();
    if (gumballMachine.getCount() > 0) {
      gumballMachine.setState(gumballMachine.getNoQuarterState());
    } else {
      System.out.println("Oops, out of gumballs!");
      gumballMachine.setState(gumballMachine.getSoldOutState());
    }
  }
}

public class HasQuarterState implements State {
  Random randomWinner = new Random(System.currentTimeMillis());
  GumballMachine gumballMachine;

  public HasQuarterState(GumballMachine gumballMachine) {
    this.gumballMachine = gumballMachine;
  }

  public void insertQuarter() {
    System.out.println("You can't insert another quarter");
  }

  public void ejectQuarter() {
    System.out.println("Quarter returned");
    gumballMachine.setState(gumballMachine.getNoQuarterState());
  }

  public void turnCrank() {
    System.out.println("You turned...");
    int winner = randomWinner.nextInt(10);
    // If 10% chance hit and there's enough stock, go to WinnerState
    if ((winner == 0) && (gumballMachine.getCount() > 1)) {
      gumballMachine.setState(gumballMachine.getWinnerState());
    } else {
      gumballMachine.setState(gumballMachine.getSoldState());
    }
  }

  public void dispense() {
    System.out.println("No gumball dispensed");
  }
}

public class WinnerState implements State {
  GumballMachine gumballMachine;

  public WinnerState(GumballMachine gumballMachine) {
    this.gumballMachine = gumballMachine;
  }

  // Error handling for illegal actions in this state
  public void insertQuarter() {
    System.out.println("Please wait, we're already giving you a gumball");
  }

  public void ejectQuarter() {
    System.out.println("Sorry, you already turned the crank");
  }

  public void turnCrank() {
    System.out.println("Turning twice doesn't get you another gumball!");
  }

  public void dispense() {
    gumballMachine.releaseBall();
    if (gumballMachine.getCount() == 0) {
      gumballMachine.setState(gumballMachine.getSoldOutState());
    } else {
      gumballMachine.releaseBall(); // Dispense the SECOND (free) ball
      System.out.println("YOU'RE A WINNER! You got two gumballs for your quarter");
      if (gumballMachine.getCount() > 0) {
        gumballMachine.setState(gumballMachine.getNoQuarterState());
      } else {
        gumballMachine.setState(gumballMachine.getSoldOutState());
      }
    }
  }
}
