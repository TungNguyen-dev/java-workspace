void main() {
  WeatherData weatherData = new WeatherData();

  // Create a display and link it to the weatherData
  CurrentConditionsDisplay currentDisplay = new CurrentConditionsDisplay(weatherData);

  // Simulating weather changes
  System.out.println("--- First Update ---");
  weatherData.setMeasurements(80, 65, 30.4f);

  System.out.println("\n--- Second Update ---");
  weatherData.setMeasurements(82, 70, 29.2f);

  // We can stop the display from receiving updates at any time
  weatherData.removeObserver(currentDisplay);

  System.out.println("\n--- Third Update (Observer removed) ---");
  weatherData.setMeasurements(78, 90, 29.2f);
}

// The Subject interface
interface Subject {
  void registerObserver(Observer o);

  void removeObserver(Observer o);

  void notifyObservers();
}

// The Observer interface - all displays must implement this
interface Observer {
  void update(float temp, float humidity, float pressure);
}

// Interface for visual elements
interface DisplayElement {
  void display();
}

// The concrete subject
class WeatherData implements Subject {
  private List<Observer> observers;
  private float temperature;
  private float humidity;
  private float pressure;

  public WeatherData() {
    observers = new ArrayList<>();
  }

  public void registerObserver(Observer o) {
    observers.add(o);
  }

  public void removeObserver(Observer o) {
    observers.remove(o);
  }

  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update(temperature, humidity, pressure);
    }
  }

  public void measurementsChanged() {
    notifyObservers();
  }

  // Setters to simulate physical device updates
  public void setMeasurements(float temperature, float humidity, float pressure) {
    this.temperature = temperature;
    this.humidity = humidity;
    this.pressure = pressure;
    measurementsChanged();
  }
}

// The concrete observer
class CurrentConditionsDisplay implements Observer, DisplayElement {
  private float temperature;
  private float humidity;
  private Subject weatherData;

  public CurrentConditionsDisplay(Subject weatherData) {
    this.weatherData = weatherData;
    weatherData.registerObserver(this); // Automatic subscription
  }

  public void update(float temperature, float humidity, float pressure) {
    this.temperature = temperature;
    this.humidity = humidity;
    display();
  }

  public void display() {
    System.out.println(
        "Current conditions: " + temperature + "F degrees and " + humidity + "% humidity");
  }
}
