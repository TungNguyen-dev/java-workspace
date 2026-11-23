void main() {
  ProductCreator product1Creator = new FirstProductCreator();
  Product product1 = product1Creator.createProduct();
  System.out.println(product1.getClass().getSimpleName());

  ProductCreator product2Creator = new SecondProductCreator();
  Product product2 = product2Creator.createProduct();
  System.out.println(product2.getClass().getSimpleName());
}

// ---

public interface ProductCreator {
  // Abstract FactoryMethod
  Product createProduct();
}

public abstract class Product {}

public class FirstProduct extends Product {}

// ---

public class SecondProduct extends Product {}

public class FirstProductCreator implements ProductCreator {
  @Override
  public Product createProduct() {
    return new FirstProduct();
  }
}

public class SecondProductCreator implements ProductCreator {
  @Override
  public Product createProduct() {
    return new SecondProduct();
  }
}
