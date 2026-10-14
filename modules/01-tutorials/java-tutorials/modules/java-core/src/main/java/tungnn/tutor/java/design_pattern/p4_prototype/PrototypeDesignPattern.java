void main(String[] args) {
  PrototypeRegistry cache = new PrototypeRegistry();

  ConcretePrototypeCircle shape1 = cache.get("Big red circle");
  ConcretePrototypeCircle shape2 = cache.get("Big red circle");

  if (shape1 != shape2 && shape1.equals(shape2)) {
    System.out.println("Hai hình là object khác nhau nhưng nội dung giống nhau!");
  } else {
    System.out.println("Có lỗi trong quá trình copy.");
  }
}

// =========================
// 1. Prototype Interface
// =========================
public interface Prototype<T> {
  T clone();
}

// =========================
// 2. Concrete Prototypes
// =========================
public static class ConcretePrototypeCircle implements Prototype<ConcretePrototypeCircle> {
  private int x;
  private int y;
  private String color;
  private int radius;

  public ConcretePrototypeCircle() {}

  public ConcretePrototypeCircle(ConcretePrototypeCircle target) {
    if (target != null) {
      this.x = target.x;
      this.y = target.y;
      this.color = target.color;
      this.radius = target.radius;
    }
  }

  @Override
  public ConcretePrototypeCircle clone() {
    return new ConcretePrototypeCircle(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConcretePrototypeCircle)) return false;
    ConcretePrototypeCircle other = (ConcretePrototypeCircle) obj;
    return x == other.x
        && y == other.y
        && radius == other.radius
        && Objects.equals(color, other.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, color, radius);
  }
}

public static class ConcretePrototypeRectangle implements Prototype<ConcretePrototypeRectangle> {
  private int x;
  private int y;
  private String color;
  private int width;
  private int height;

  public ConcretePrototypeRectangle() {}

  public ConcretePrototypeRectangle(ConcretePrototypeRectangle target) {
    if (target != null) {
      this.x = target.x;
      this.y = target.y;
      this.color = target.color;
      this.width = target.width;
      this.height = target.height;
    }
  }

  @Override
  public ConcretePrototypeRectangle clone() {
    return new ConcretePrototypeRectangle(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConcretePrototypeRectangle)) return false;
    ConcretePrototypeRectangle other = (ConcretePrototypeRectangle) obj;
    return x == other.x
        && y == other.y
        && width == other.width
        && height == other.height
        && Objects.equals(color, other.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, color, width, height);
  }
}

// =========================
// 3. Prototype Registry
// =========================
public static class PrototypeRegistry {
  private final Map<String, Prototype<?>> cache = new HashMap<>();

  public PrototypeRegistry() {
    ConcretePrototypeCircle circle = new ConcretePrototypeCircle();
    circle.x = 10;
    circle.y = 20;
    circle.color = "Red";
    circle.radius = 15;

    ConcretePrototypeRectangle rectangle = new ConcretePrototypeRectangle();
    rectangle.x = 0;
    rectangle.y = 0;
    rectangle.color = "Blue";
    rectangle.width = 10;
    rectangle.height = 20;

    cache.put("Big red circle", circle);
    cache.put("Medium blue rectangle", rectangle);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    Prototype<?> prototype = cache.get(key);
    if (prototype == null) return null;
    return (T) prototype.clone();
  }
}
