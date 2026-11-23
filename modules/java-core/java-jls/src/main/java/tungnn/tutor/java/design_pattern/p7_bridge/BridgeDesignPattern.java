void main(String[] args) {
  System.out.println("--- Thử nghiệm với TV và Remote cơ bản ---");
  Device tv = new TvDevice();
  RemoteControl basicRemote = new RemoteControl(tv);
  basicRemote.togglePower();
  basicRemote.volumeUp();

  System.out.println("\n--- Thử nghiệm với Radio và Remote nâng cao ---");
  Device radio = new RadioDevice();
  AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);
  advancedRemote.togglePower();
  advancedRemote.mute(); // Tính năng chỉ có ở AdvancedRemote
}

// Implementation Interface
public interface Device {
  boolean isEnabled();

  void enable();

  void disable();

  int getVolume();

  void setVolume(int percent);
}

// Concrete Implementation 1: TV
public class TvDevice implements Device {
  private boolean on = false;
  private int volume = 30;

  @Override
  public boolean isEnabled() {
    return on;
  }

  @Override
  public void enable() {
    on = true;
    System.out.println("TV: Đã bật");
  }

  @Override
  public void disable() {
    on = false;
    System.out.println("TV: Đã tắt");
  }

  @Override
  public int getVolume() {
    return volume;
  }

  @Override
  public void setVolume(int percent) {
    this.volume = percent;
    System.out.println("TV: Âm lượng = " + volume);
  }
}

// Concrete Implementation 2: Radio
public class RadioDevice implements Device {
  private boolean on = false;
  private int volume = 10;

  @Override
  public boolean isEnabled() {
    return on;
  }

  @Override
  public void enable() {
    on = true;
    System.out.println("Radio: Đã bật");
  }

  @Override
  public void disable() {
    on = false;
    System.out.println("Radio: Đã tắt");
  }

  @Override
  public int getVolume() {
    return volume;
  }

  @Override
  public void setVolume(int percent) {
    this.volume = percent;
    System.out.println("Radio: Âm lượng = " + volume);
  }
}

// Abstraction
public class RemoteControl {

  protected Device device; // "The Bridge"

  public RemoteControl(Device device) {
    this.device = device;
  }

  public void togglePower() {
    if (device.isEnabled()) {
      device.disable();
    } else {
      device.enable();
    }
  }

  public void volumeDown() {
    device.setVolume(device.getVolume() - 10);
  }

  public void volumeUp() {
    device.setVolume(device.getVolume() + 10);
  }
}

// Refined Abstraction: Điều khiển nâng cao có thêm nút Mute
public class AdvancedRemoteControl extends RemoteControl {
  public AdvancedRemoteControl(Device device) {
    super(device);
  }

  public void mute() {
    System.out.println("Remote: Đang tắt tiếng...");
    device.setVolume(0);
  }
}
