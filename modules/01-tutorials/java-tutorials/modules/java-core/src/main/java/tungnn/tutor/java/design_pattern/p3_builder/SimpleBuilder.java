void main() {
  var simpleWoodHouse = House.woodHouse().hasSwimmingPool(false).build();
  System.out.println(simpleWoodHouse);

  var luxuryWoodHouse = House.woodHouse().hasSwimmingPool(true).build();
  System.out.println(luxuryWoodHouse);
}

public record House(String walls, String doors, String roof, boolean hasSwimmingPool) {

  public static Builder builder() {
    return new Builder();
  }

  public static Builder woodHouse() {
    return builder().walls("Wood Walls").doors("Wood Door").roof("Wood Roof");
  }

  public static class Builder {
    private String walls;
    private String doors;
    private String roof;
    private boolean hasSwimmingPool;

    public Builder walls(String walls) {
      this.walls = walls;
      return this;
    }

    public Builder doors(String doors) {
      this.doors = doors;
      return this;
    }

    public Builder roof(String roof) {
      this.roof = roof;
      return this;
    }

    public Builder hasSwimmingPool(boolean value) {
      this.hasSwimmingPool = value;
      return this;
    }

    public House build() {
      if (walls == null || doors == null || roof == null) {
        throw new IllegalStateException("walls, doors, roof are required");
      }
      return new House(walls, doors, roof, hasSwimmingPool);
    }
  }
}
