// Client chỉ chấp nhận kiểu Duck
static void testDuck(DuckTargetInterface duck) {
  duck.quack();
  duck.fly();
}

void main(String[] args) {
  // Tạo một con gà tây thực thụ
  WildTurkeyAdaptee turkey = new WildTurkeyAdaptee();

  // Bọc con gà tây vào một cái Adapter để nó trông như một con vịt
  DuckTargetInterface turkeyAdapter = new TurkeyAdapter(turkey);

  System.out.println("The Turkey says...");
  turkey.gobble();
  turkey.fly();

  System.out.println("\nThe TurkeyAdapter says (as a Duck):");
  testDuck(turkeyAdapter);
}

// Target Interface
public interface DuckTargetInterface {
  void quack();

  void fly();
}

// Adapter Class
public static class TurkeyAdapter implements DuckTargetInterface {
  private WildTurkeyAdaptee turkey;

  public TurkeyAdapter(WildTurkeyAdaptee turkey) {
    this.turkey = turkey;
  }

  @Override
  public void quack() {
    // Chuyển đổi yêu cầu quack() thành gobble()
    turkey.gobble();
  }

  @Override
  public void fly() {
    // Gà tây bay ngắn hơn vịt, nên ta cho nó bay 5 lần để bù đắp quãng đường
    for (int i = 0; i < 5; i++) {
      turkey.fly();
    }
  }
}

// Adaptee Class
public static class WildTurkeyAdaptee {
  public void gobble() {
    System.out.println("Gobble gobble");
  }

  public void fly() {
    System.out.println("I'm flying a short distance");
  }
}
