void main(String[] args) {
  Foo foo1 = Foo.getInstance();
  Foo foo2 = Foo.getInstance();

  if (foo1 == foo2) {
    System.out.println("Foo Singleton works");
  }
}

static class Foo {

  public static Foo getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    private static final Foo INSTANCE = new Foo();
  }
}
