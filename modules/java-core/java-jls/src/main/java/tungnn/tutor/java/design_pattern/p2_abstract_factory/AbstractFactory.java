void main(String[] args) {
    FurnitureFactory factory;

    // Assume the system configuration requires the Modern style
    String config = args[0];

    if (config.equals("MODERN")) {
        factory = new ModernFurnitureFactory();
    } else {
        factory = new VictorianFurnitureFactory();
    }

    // Create the client with the chosen factory
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
        // The client only knows the interface, not the concrete class (Modern or Victorian)
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
        System.out.println("Sitting on a modern chair.");
    }
}

public class ModernSofa implements Sofa {
    public void lieOn() {
        System.out.println("Lying on a modern sofa.");
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
        System.out.println("Sitting on a carved classic chair.");
    }
}

public class VictorianSofa implements Sofa {
    public void lieOn() {
        System.out.println("Lying on a royal classic sofa.");
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
