void main() {
  // 1. Create the object structure (The Neighborhood)
  List<Building> neighborhood = new ArrayList<>();
  neighborhood.add(new ResidentialBuilding());
  neighborhood.add(new Bank());
  neighborhood.add(new CoffeeShop());

  // 2. Create the visitor (The Insurance Agent)
  InsuranceAgent agent = new InsuranceAgent();

  // 3. Perform the operation across the structure
  System.out.println("--- Insurance Agent Round ---");
  for (Building building : neighborhood) {
    building.accept(agent);
  }
}

// The Visitor Interface
public interface InsuranceVisitor {
  void visit(ResidentialBuilding residential);

  void visit(Bank bank);

  void visit(CoffeeShop coffeeShop);
}

// The Element Interface
public interface Building {
  void accept(InsuranceVisitor visitor);
}

// Concrete Visitor
public class InsuranceAgent implements InsuranceVisitor {
  @Override
  public void visit(ResidentialBuilding residential) {
    System.out.println("Selling medical insurance to the residents.");
  }

  @Override
  public void visit(Bank bank) {
    System.out.println("Selling professional theft and liability insurance to the bank.");
  }

  @Override
  public void visit(CoffeeShop coffeeShop) {
    System.out.println("Selling fire and flood insurance to the coffee shop.");
  }
}

// Concrete Elements
public class ResidentialBuilding implements Building {
  @Override
  public void accept(InsuranceVisitor visitor) {
    visitor.visit(this); // Redirects to the residential-specific logic
  }
}

public class Bank implements Building {
  @Override
  public void accept(InsuranceVisitor visitor) {
    visitor.visit(this);
  }
}

public class CoffeeShop implements Building {
  @Override
  public void accept(InsuranceVisitor visitor) {
    visitor.visit(this);
  }
}
