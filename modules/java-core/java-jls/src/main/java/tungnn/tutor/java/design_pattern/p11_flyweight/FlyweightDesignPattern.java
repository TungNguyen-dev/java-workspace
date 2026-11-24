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
    // Plant thousands of trees, yet only 2 TreeType objects are actually created
    plantTree(1, 2, "Oak", "Green", "Texture_Oak_4K");
    plantTree(5, 3, "Oak", "Green", "Texture_Oak_4K");
    plantTree(10, 10, "Pine", "Dark green", "Texture_Pine_4K");
    plantTree(20, 30, "Oak", "Green", "Texture_Oak_4K");

    draw();

    System.out.println("--------------------------------------");
    System.out.println("Total trees planted: 4");
    System.out.println("Heavy data (TreeType) actually in memory: 2");
}

// Flyweight: holds the intrinsic state
public record TreeType(String name, String color, String otherTreeData) {

    public void draw(int x, int y) {
        System.out.println("Drawing tree " + name + " [Color: " + color + "] at coordinates (" + x + "," + y + ")");
    }
}

// Context: holds the extrinsic state
class Tree {
    private final int x;
    private final int y;
    private final TreeType type; // Reference to the flyweight

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
    private static final Map<String, TreeType> treeTypes = new HashMap<>();

    public static TreeType getTreeType(String name, String color, String otherTreeData) {
        String key = name + "_" + color;
        TreeType result = treeTypes.get(key);

        if (result == null) {
            result = new TreeType(name, color, otherTreeData);
            treeTypes.put(key, result);
            System.out.println("== Creating new tree type: " + name + " ==");
        }
        return result;
    }
}
