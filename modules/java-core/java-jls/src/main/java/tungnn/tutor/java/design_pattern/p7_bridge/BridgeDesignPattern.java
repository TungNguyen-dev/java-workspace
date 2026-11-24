void main(String[] args) {
    System.out.println("--- Trying a TV with a basic remote ---");
    Device tv = new TvDevice();
    RemoteControl basicRemote = new RemoteControl(tv);
    basicRemote.togglePower();
    basicRemote.volumeUp();

    System.out.println("\n--- Trying a radio with an advanced remote ---");
    Device radio = new RadioDevice();
    AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);
    advancedRemote.togglePower();
    advancedRemote.mute(); // Feature that only AdvancedRemote has
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
        System.out.println("TV: Turned on");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("TV: Turned off");
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int percent) {
        this.volume = percent;
        System.out.println("TV: Volume = " + volume);
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
        System.out.println("Radio: Turned on");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Radio: Turned off");
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int percent) {
        this.volume = percent;
        System.out.println("Radio: Volume = " + volume);
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

// Refined Abstraction: advanced remote with an extra Mute button
public class AdvancedRemoteControl extends RemoteControl {
    public AdvancedRemoteControl(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Remote: Muting...");
        device.setVolume(0);
    }
}
