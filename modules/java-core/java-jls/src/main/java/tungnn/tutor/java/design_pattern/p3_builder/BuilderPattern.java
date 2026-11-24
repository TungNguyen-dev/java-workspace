void main() {
    HouseDirector director = new HouseDirector();

    // Build a simple wooden house
    HouseBuilder woodBuilder = new WoodHouseBuilder();
    director.constructSimpleHouse(woodBuilder);
    House simpleWoodHouse = woodBuilder.getResult();

    System.out.println("Result: " + simpleWoodHouse);
}

interface HouseBuilder {
    void buildWalls();

    void buildDoors();

    void buildRoof();

    void buildPool();

    House getResult();
}

class House {
    private String walls;
    private String doors;
    private String roof;
    private boolean hasSwimmingPool;

    // Setters used by the builder
    public void setWalls(String walls) {
        this.walls = walls;
    }

    public void setDoors(String doors) {
        this.doors = doors;
    }

    public void setRoof(String roof) {
        this.roof = roof;
    }

    public void setHasSwimmingPool(boolean hasSwimmingPool) {
        this.hasSwimmingPool = hasSwimmingPool;
    }

    @Override
    public String toString() {
        return "House [Walls="
                + walls
                + ", Doors="
                + doors
                + ", Roof="
                + roof
                + ", Pool="
                + (hasSwimmingPool ? "Yes" : "No")
                + "]";
    }
}

class HouseDirector {
    public void constructLuxuryHouse(HouseBuilder builder) {
        builder.buildWalls();
        builder.buildDoors();
        builder.buildRoof();
        builder.buildPool(); // A luxury house gets a swimming pool
    }

    public void constructSimpleHouse(HouseBuilder builder) {
        builder.buildWalls();
        builder.buildDoors();
        builder.buildRoof();
        // Do not call buildPool
    }
}

class WoodHouseBuilder implements HouseBuilder {
    private final House house = new House();

    @Override
    public void buildWalls() {
        house.setWalls("Wood Walls");
    }

    @Override
    public void buildDoors() {
        house.setDoors("Wood Door");
    }

    @Override
    public void buildRoof() {
        house.setRoof("Wood Roof");
    }

    @Override
    public void buildPool() {
        house.setHasSwimmingPool(false);
    }

    @Override
    public House getResult() {
        return this.house;
    }
}
