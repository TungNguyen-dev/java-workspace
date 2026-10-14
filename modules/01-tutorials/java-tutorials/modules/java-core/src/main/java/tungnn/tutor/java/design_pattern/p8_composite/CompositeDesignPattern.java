void main() {
  // Tạo các Menu cấp cao
  MenuComponent pancakeHouseMenu = new Menu("PANCAKE HOUSE MENU");
  MenuComponent dinerMenu = new Menu("DINER MENU");
  MenuComponent dessertMenu = new Menu("DESSERT MENU");

  // Cấu trúc cây chính
  MenuComponent allMenus = new Menu("ALL MENUS");
  allMenus.add(pancakeHouseMenu);
  allMenus.add(dinerMenu);

  // Thêm các Leaf (món ăn)
  pancakeHouseMenu.add(new MenuItem("K&B's Pancake Breakfast", 2.99));
  dinerMenu.add(new MenuItem("Vegetarian BLT", 2.99));
  dinerMenu.add(new MenuItem("Pasta", 3.89));

  // Thêm Composite vào Composite (Menu trong Menu)
  dinerMenu.add(dessertMenu);
  dessertMenu.add(new MenuItem("Apple Pie", 1.59));

  // Client xử lý đồng nhất toàn bộ cấu trúc
  allMenus.print();
}

public abstract static class MenuComponent {
  // Các phương thức quản lý con (cho Composite)
  public void add(MenuComponent menuComponent) {
    throw new UnsupportedOperationException();
  }

  public void remove(MenuComponent menuComponent) {
    throw new UnsupportedOperationException();
  }

  public MenuComponent getChild(int i) {
    throw new UnsupportedOperationException();
  }

  // Các phương thức hoạt động (cho cả Leaf và Composite)
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

    // Sử dụng đệ quy để in tất cả các thành phần con
    for (MenuComponent component : menuComponents) {
      component.print();
    }
  }
}

public static class MenuItem extends MenuComponent {
  private String name;
  private double price;

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
