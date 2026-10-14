void main() {
  Traveler traveler = new Traveler();
  String currentPos = "Downtown";

  // 1. Client decides to take the bus (Budget friendly)
  traveler.setStrategy(new PublicTransportStrategy());
  traveler.goToAirport(currentPos);

  // 2. It starts raining! Client switches to a Taxi at runtime
  System.out.println("\n--- Update: It's raining! ---");
  traveler.setStrategy(new TaxiStrategy());
  traveler.goToAirport(currentPos);
}

// The Strategy Interface
public interface RouteStrategy {
  void buildRoute(String start, String destination);
}

// Concrete Strategy A
public class PublicTransportStrategy implements RouteStrategy {
  @Override
  public void buildRoute(String start, String destination) {
    System.out.println("Finding bus lines from " + start + " to " + destination + ".");
  }
}

// Concrete Strategy B
public class TaxiStrategy implements RouteStrategy {
  @Override
  public void buildRoute(String start, String destination) {
    System.out.println("Ordering a cab. Estimated cost: $30 to get to " + destination + ".");
  }
}

// Concrete Strategy C
public class BikeStrategy implements RouteStrategy {
  @Override
  public void buildRoute(String start, String destination) {
    System.out.println("Calculating bike paths. Ensure your helmet is ready!");
  }
}

public class Traveler {
  private RouteStrategy strategy;

  // The Context Setter (allows runtime switching)
  public void setStrategy(RouteStrategy strategy) {
    this.strategy = strategy;
  }

  public void goToAirport(String location) {
    if (strategy == null) {
      System.out.println("Please select a transport method first!");
    } else {
      // Delegation: The context doesn't implement the logic, it asks the strategy
      strategy.buildRoute(location, "International Airport");
    }
  }
}
