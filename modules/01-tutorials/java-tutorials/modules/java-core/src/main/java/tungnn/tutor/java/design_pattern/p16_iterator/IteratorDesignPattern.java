void main() {
  // 1. Initialize the two different menu implementations
  PancakeHouseMenu pancakeHouseMenu = new PancakeHouseMenu();
  DinerMenu dinerMenu = new DinerMenu();

  // 2. Instantiate the Waitress with the menus
  // Note: The Waitress doesn't know about the internal storage (ArrayList vs Array)
  Waitress waitress = new Waitress(pancakeHouseMenu, dinerMenu);

  // 3. Execute the unified print method
  waitress.printMenu();
}

// The Common Item
public record MenuItem(String name, String description, boolean vegetarian, double price) {}

// Concrete Iterator: DinerMenu (Array-based)
public class DinerMenuIterator implements Iterator<MenuItem> {
  MenuItem[] items;
  int position = 0;

  public DinerMenuIterator(MenuItem[] items) {
    this.items = items;
  }

  public MenuItem next() {
    MenuItem menuItem = items[position];
    position = position + 1;
    return menuItem;
  }

  public boolean hasNext() {
    // Check if we hit the end of array or a null entry (per Mel's logic)
    if (position >= items.length || items[position] == null) {
      return false;
    } else {
      return true;
    }
  }
}

// The Aggregate Interface
public class PancakeHouseMenu {
  ArrayList<MenuItem> menuItems;

  public PancakeHouseMenu() {
    menuItems = new ArrayList<>();

    addItem("K&B's Pancake Breakfast", "Pancakes with scrambled eggs and toast", true, 2.99);
    addItem("Regular Pancake Breakfast", "Pancakes with fried eggs and sausage", false, 2.99);
    addItem("Blueberry Pancakes", "Pancakes made with fresh blueberries", true, 3.49);
    addItem("Waffles", "Waffles with your choice of blueberries or strawberries", true, 3.59);
  }

  public Iterator<MenuItem> createIterator() {
    return menuItems.iterator(); // Returns standard ArrayList iterator
  }

  public void addItem(String name, String description, boolean vegetarian, double price) {
    MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
    menuItems.add(menuItem);
  }
}

public class DinerMenu {
  static final int MAX_ITEMS = 6;
  int numberOfItems = 0;
  MenuItem[] menuItems;

  public DinerMenu() {
    menuItems = new MenuItem[MAX_ITEMS];

    addItem("K&B's Pancake Breakfast", "Pancakes with scrambled eggs and toast", true, 2.99);
    addItem("Regular Pancake Breakfast", "Pancakes with fried eggs and sausage", false, 2.99);
    addItem("Blueberry Pancakes", "Pancakes made with fresh blueberries", true, 3.49);
    addItem("Waffles", "Waffles with your choice of blueberries or strawberries", true, 3.59);
  }

  public Iterator<MenuItem> createIterator() {
    return new DinerMenuIterator(menuItems); // Returns our custom iterator
  }

  public void addItem(String name, String description, boolean vegetarian, double price) {
    MenuItem menuItem = new MenuItem(name, description, vegetarian, price);

    if (numberOfItems >= MAX_ITEMS) {
      System.err.println("Sorry, menu is full! Can't add item to diner menu");
    } else {
      menuItems[numberOfItems] = menuItem;
      numberOfItems = numberOfItems + 1;
    }
  }
}

// The Unified Client: The Waitress
public class Waitress {
  PancakeHouseMenu pancakeHouseMenu;
  DinerMenu dinerMenu;

  public Waitress(PancakeHouseMenu pancakeHouseMenu, DinerMenu dinerMenu) {
    this.pancakeHouseMenu = pancakeHouseMenu;
    this.dinerMenu = dinerMenu;
  }

  public void printMenu() {
    Iterator<MenuItem> pancakeIterator = pancakeHouseMenu.createIterator();
    Iterator<MenuItem> dinerIterator = dinerMenu.createIterator();

    System.out.println("MENU\n----\nBREAKFAST");
    printMenu(pancakeIterator);
    System.out.println("\nLUNCH");
    printMenu(dinerIterator);
  }

  // Overloaded polymorphic method
  private void printMenu(Iterator<MenuItem> iterator) {
    while (iterator.hasNext()) {
      MenuItem menuItem = iterator.next();
      System.out.print(menuItem.name() + ", ");
      System.out.print(menuItem.price() + " -- ");
      System.out.println(menuItem.description());
    }
  }
}
