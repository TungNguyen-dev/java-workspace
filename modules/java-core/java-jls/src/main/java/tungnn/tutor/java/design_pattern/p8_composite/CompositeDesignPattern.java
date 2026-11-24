void main() {
  // Create the top-level menus
  MenuComponent pancakeHouseMenu = new Menu("PANCAKE HOUSE MENU");
  MenuComponent dinerMenu = new Menu("DINER MENU");
  MenuComponent dessertMenu = new Menu("DESSERT MENU");

  // Main tree structure
  MenuComponent allMenus = new Menu("ALL MENUS");
  allMenus.add(pancakeHouseMenu);
  allMenus.add(dinerMenu);

  // Add the leaves (dishes)
  pancakeHouseMenu.add(new MenuItem("K&B's Pancake Breakfast", 2.99));
  dinerMenu.add(new MenuItem("Vegetarian BLT", 2.99));
  dinerMenu.add(new MenuItem("Pasta", 3.89));

  // Add a composite to a composite (menu inside a menu)
  dinerMenu.add(dessertMenu);
  dessertMenu.add(new MenuItem("Apple Pie", 1.59));

  // The client treats the whole structure uniformly
  allMenus.print();
}

public abstract static class MenuComponent {
  // Child management methods (for Composite)
  public void add(MenuComponent menuComponent) {
    throw new UnsupportedOperationException();
  }

  public void remove(MenuComponent menuComponent) {
    throw new UnsupportedOperationException();
  }

  public MenuComponent getChild(int i) {
    throw new UnsupportedOperationException();
  }

  // Operation methods (for both Leaf and Composite)
  public String getName() {
    throw new UnsupportedOperationException();
  }

  public double getPrice() {
    throw new UnsupportedOperationException();
  }

  public abstract void print();
}

public static class Menu extends MenuComponent {

  private final List<MenuComponent> menuComponents = new ArrayList<>();
  private final String name;

  public Menu(String name) {
    this.name = name;
  }

  @Override
  public void add(MenuComponent menuComponent) {
    menuComponents.add(menuComponent);
  }

  @Override
  public void remove(MenuComponent menuComponent) {
    menuComponents.remove(menuComponent);
  }

  @Override
  public MenuComponent getChild(int i) {
    return menuComponents.get(i);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void print() {
    System.out.println("\nMENU: " + getName());
    System.out.println("---------------------");

    // Use recursion to print all child components
    for (MenuComponent component : menuComponents) {
      component.print();
    }
  }
}

public static class MenuItem extends MenuComponent {
  private final String name;
  private final double price;

  public MenuItem(String name, double price) {
    this.name = name;
    this.price = price;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public void print() {
    System.out.println("  " + getName() + ", " + getPrice() + " USD");
  }
}
