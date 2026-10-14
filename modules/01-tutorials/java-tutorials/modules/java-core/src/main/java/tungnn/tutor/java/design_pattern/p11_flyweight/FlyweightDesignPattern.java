List<Tree> trees = new ArrayList<>();

public void plantTree(int x, int y, String name, String color, String otherTreeData) {
  TreeType type = TreeFactory.getTreeType(name, color, otherTreeData);
  Tree tree = new Tree(x, y, type);
  trees.add(tree);
}

public void draw() {
  for (Tree tree : trees) {
    tree.draw();
  }
}

void main(String[] args) {
  // Trồng hàng ngàn cây nhưng thực tế chỉ có 2 loại đối tượng TreeType được tạo ra
  plantTree(1, 2, "Sồi", "Xanh lá", "Texture_Sồi_4K");
  plantTree(5, 3, "Sồi", "Xanh lá", "Texture_Sồi_4K");
  plantTree(10, 10, "Thông", "Xanh thẫm", "Texture_Thông_4K");
  plantTree(20, 30, "Sồi", "Xanh lá", "Texture_Sồi_4K");

  draw();

  System.out.println("--------------------------------------");
  System.out.println("Tổng số cây đã trồng: 4");
  System.out.println("Dữ liệu nặng (TreeType) thực tế trong bộ nhớ: 2");
}

// Flyweight: Chứa trạng thái nội tại (Intrinsic State)
public record TreeType(String name, String color, String otherTreeData) {

  public void draw(int x, int y) {
    System.out.println("Vẽ cây " + name + " [Màu: " + color + "] tại tọa độ (" + x + "," + y + ")");
  }
}

// Context: Chứa trạng thái ngoại lai (Extrinsic State)
class Tree {
  private int x;
  private int y;
  private TreeType type; // Tham chiếu đến Flyweight

  public Tree(int x, int y, TreeType type) {
    this.x = x;
    this.y = y;
    this.type = type;
  }

  public void draw() {
    type.draw(x, y);
  }
}

// Flyweight Factory
class TreeFactory {
  private static Map<String, TreeType> treeTypes = new HashMap<>();

  public static TreeType getTreeType(String name, String color, String otherTreeData) {
    String key = name + "_" + color;
    TreeType result = treeTypes.get(key);

    if (result == null) {
      result = new TreeType(name, color, otherTreeData);
      treeTypes.put(key, result);
      System.out.println("== Tạo mới loại cây: " + name + " ==");
    }
    return result;
  }
}
