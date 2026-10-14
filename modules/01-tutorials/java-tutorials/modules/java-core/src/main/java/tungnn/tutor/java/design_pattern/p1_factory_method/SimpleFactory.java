void main() {
  ProductCreator productCreator = new ProductCreator();
  Product product = productCreator.createProduct();
  System.out.println(product.getClass().getSimpleName());
}

// ---

public class Product {}

public class ProductCreator {
  // Simple factory method
  public Product createProduct() {
    return new Product();
  }
}
