void main(String[] args) {
  FurnitureFactory factory;

  // Giả sử cấu hình hệ thống yêu cầu phong cách Modern
  String config = args[0];

  if (config.equals("MODERN")) {
    factory = new ModernFurnitureFactory();
  } else {
    factory = new VictorianFurnitureFactory();
  }

  // Khởi tạo Client với nhà máy đã chọn
  FurnitureShop shop = new FurnitureShop(factory);
  shop.displayFurniture();
}

public interface Chair {
  void sitOn();
}

public interface Sofa {
  void lieOn();
}

public interface FurnitureFactory {

  Chair createChair();

  Sofa createSofa();
}

public class FurnitureShop {

  private final Chair chair;
  private final Sofa sofa;

  public FurnitureShop(FurnitureFactory factory) {
    // Client chỉ biết về interface, không biết về lớp cụ thể (Modern hay Victorian)
    chair = factory.createChair();
    sofa = factory.createSofa();
  }

  public void displayFurniture() {
    chair.sitOn();
    sofa.lieOn();
  }
}

public class ModernChair implements Chair {
  public void sitOn() {
    System.out.println("Đang ngồi trên ghế hiện đại.");
  }
}

public class ModernSofa implements Sofa {
  public void lieOn() {
    System.out.println("Đang nằm trên sofa hiện đại.");
  }
}

public class ModernFurnitureFactory implements FurnitureFactory {

  public Chair createChair() {
    return new ModernChair();
  }

  public Sofa createSofa() {
    return new ModernSofa();
  }
}

public class VictorianChair implements Chair {
  public void sitOn() {
    System.out.println("Đang ngồi trên ghế cổ điển chạm trổ.");
  }
}

public class VictorianSofa implements Sofa {
  public void lieOn() {
    System.out.println("Đang nằm trên sofa cổ điển hoàng gia.");
  }
}

public class VictorianFurnitureFactory implements FurnitureFactory {

  public Chair createChair() {
    return new VictorianChair();
  }

  public Sofa createSofa() {
    return new VictorianSofa();
  }
}
