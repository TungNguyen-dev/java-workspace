void main() {
  // Khởi tạo các thành phần subsystem
  Projector projector = new Projector();
  Amplifier amp = new Amplifier();
  Screen screen = new Screen();
  PopcornPopper popper = new PopcornPopper();

  // Tạo Facade
  HomeTheaterFacade homeTheater = new HomeTheaterFacade(projector, amp, screen, popper);

  // Sử dụng giao diện đơn giản
  homeTheater.watchMovie("Inception");

  // Khi xem xong
  homeTheater.endMovie();
}

// Facade
static class HomeTheaterFacade {
  private Projector projector;
  private Amplifier amp;
  private Screen screen;
  private PopcornPopper popper;

  public HomeTheaterFacade(
      Projector projector, Amplifier amp, Screen screen, PopcornPopper popper) {
    this.projector = projector;
    this.amp = amp;
    this.screen = screen;
    this.popper = popper;
  }

  // Phương thức đơn giản hóa thay vì phải gọi hàng chục lệnh lẻ tẻ
  public void watchMovie(String movie) {
    System.out.println("--- Get ready to watch: " + movie + " ---");
    popper.on();
    popper.pop();
    screen.down();
    projector.on();
    projector.wideScreenMode();
    amp.on();
    amp.setVolume(10);
    System.out.println("--- Movie started! Enjoy! ---\n");
  }

  public void endMovie() {
    System.out.println("--- Shutting movie theater down... ---");
    popper.off();
    screen.up();
    projector.off();
    amp.off();
    System.out.println("--- Done! ---");
  }
}

// Subsystem
// Thiết bị 2: Hệ thống âm thanh (Amplifier)
static class Amplifier {
  void on() {
    System.out.println("Amplifier is ON");
  }

  void setVolume(int level) {
    System.out.println("Amplifier volume set to " + level);
  }

  void off() {
    System.out.println("Amplifier is OFF");
  }
}

// Thiết bị 4: Máy nổ bỏng ngô (PopcornPopper)
static class PopcornPopper {
  void on() {
    System.out.println("Popcorn Popper is ON");
  }

  void pop() {
    System.out.println("Popping popcorn!");
  }

  void off() {
    System.out.println("Popcorn Popper is OFF");
  }
}

// Thiết bị 1: Máy chiếu (Projector)
static class Projector {
  void on() {
    System.out.println("Projector is ON");
  }

  void wideScreenMode() {
    System.out.println("Projector set to 16:9 aspect ratio");
  }

  void off() {
    System.out.println("Projector is OFF");
  }
}

// Thiết bị 3: Màn chiếu (Screen)
static class Screen {
  void down() {
    System.out.println("Screen is lowering...");
  }

  void up() {
    System.out.println("Screen is rolling up...");
  }
}
