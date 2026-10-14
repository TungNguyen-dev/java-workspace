void main(String[] args) {
  // 1. Order một ly Espresso đơn thuần
  BeverageComponent beverage = new EspressoConcreteComponent();
  System.out.println(beverage.getDescription() + " $" + beverage.cost());

  // 2. Order một ly Dark Roast, thêm Mocha, thêm Mocha (double), rồi thêm Whip
  BeverageComponent beverage2 = new DarkRoastConcreteComponent();
  beverage2 = new MochaConcreteDecorator(beverage2); // Bọc DarkRoast trong Mocha
  beverage2 = new MochaConcreteDecorator(beverage2); // Bọc Mocha(DarkRoast) trong Mocha thứ hai
  beverage2 = new WhipConcreteDecorator(beverage2); // Bọc tất cả trong Whip

  System.out.println(beverage2.getDescription() + " $" + String.format("%.2f", beverage2.cost()));
  // Kết quả kỳ vọng: 0.99 (DarkRoast) + 0.20 (Mocha) + 0.20 (Mocha) + 0.10 (Whip) = 1.49
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

  protected BeverageComponent beverage; // Đối tượng được bao bọc (wrapped)

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
    return beverage.cost() + 0.20; // Giá cafe gốc + 0.20
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
    return beverage.cost() + 0.10; // Giá cafe trước đó + 0.10
  }
}
