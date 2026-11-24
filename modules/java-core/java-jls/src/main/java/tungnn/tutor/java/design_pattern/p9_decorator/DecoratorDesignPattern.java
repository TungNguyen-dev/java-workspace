void main(String[] args) {
    // 1. Order a plain espresso
    BeverageComponent beverage = new EspressoConcreteComponent();
    System.out.println(beverage.getDescription() + " $" + beverage.cost());

    // 2. Order a dark roast, add mocha, add mocha again (double), then add whip
    BeverageComponent beverage2 = new DarkRoastConcreteComponent();
    beverage2 = new MochaConcreteDecorator(beverage2); // Wrap DarkRoast in Mocha
    beverage2 = new MochaConcreteDecorator(beverage2); // Wrap Mocha(DarkRoast) in a second Mocha
    beverage2 = new WhipConcreteDecorator(beverage2); // Wrap everything in Whip

    System.out.println(beverage2.getDescription() + " $" + String.format("%.2f", beverage2.cost()));
    // Expected result: 0.99 (DarkRoast) + 0.20 (Mocha) + 0.20 (Mocha) + 0.10 (Whip) = 1.49
}

// Component
public abstract class BeverageComponent {
    String description = "Unknown Beverage";

    public String getDescription() {
        return description;
    }

    public abstract double cost();
}

// Concrete Components
public class DarkRoastConcreteComponent extends BeverageComponent {
    public DarkRoastConcreteComponent() {
        description = "Dark Roast Coffee";
    }

    public double cost() {
        return 0.99;
    }
}

public class EspressoConcreteComponent extends BeverageComponent {
    public EspressoConcreteComponent() {
        description = "Espresso";
    }

    public double cost() {
        return 1.99;
    }
}

// Decorator
public abstract class CondimentDecorator extends BeverageComponent {

    protected BeverageComponent beverage; // The wrapped object

    public abstract String getDescription();
}

// Concrete Decorator
public class MochaConcreteDecorator extends CondimentDecorator {
    public MochaConcreteDecorator(BeverageComponent beverage) {
        this.beverage = beverage;
    }

    public String getDescription() {
        return beverage.getDescription() + ", Mocha";
    }

    public double cost() {
        return beverage.cost() + 0.20; // Base coffee price + 0.20
    }
}

public class WhipConcreteDecorator extends CondimentDecorator {
    public WhipConcreteDecorator(BeverageComponent beverage) {
        this.beverage = beverage;
    }

    public String getDescription() {
        return beverage.getDescription() + ", Whip";
    }

    public double cost() {
        return beverage.cost() + 0.10; // Previous coffee price + 0.10
    }
}
