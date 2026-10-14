void main() {
  // Goal: Interpret the expression "x + (10 - y)"

  // 1. Build the AST (Usually done by a Parser)
  // Represents: x + (10 - y)
  Expression expression =
      new AddExpression(
          new VariableExpression("x"),
          new SubtractExpression(new NumberExpression(10), new VariableExpression("y")));

  // 2. Define the Context (Variable values)
  Map<String, Integer> context = new HashMap<>();
  context.put("x", 5);
  context.put("y", 3);

  // 3. Interpret
  // Result: 5 + (10 - 3) = 12
  int result = expression.interpret(context);
  System.out.println("The result of 'x + (10 - y)' where x=5, y=3 is: " + result);
}

// The Abstract Expression
public interface Expression {
  int interpret(Map<String, Integer> context);
}

// Terminal Expressions
// Represents a literal number (Terminal)
class NumberExpression implements Expression {
  private int number;

  public NumberExpression(int number) {
    this.number = number;
  }

  @Override
  public int interpret(Map<String, Integer> context) {
    return number;
  }
}

// Represents a variable like "x" or "y" (Terminal)
class VariableExpression implements Expression {
  private String name;

  public VariableExpression(String name) {
    this.name = name;
  }

  @Override
  public int interpret(Map<String, Integer> context) {
    if (context.containsKey(name)) {
      return context.get(name);
    }
    return 0; // Default if variable not set
  }
}

// Non-terminal Expressions
// Represents Addition rule: R ::= R1 + R2
class AddExpression implements Expression {
  private Expression left;
  private Expression right;

  public AddExpression(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public int interpret(Map<String, Integer> context) {
    return left.interpret(context) + right.interpret(context);
  }
}

// Represents Subtraction rule: R ::= R1 - R2
class SubtractExpression implements Expression {
  private Expression left;
  private Expression right;

  public SubtractExpression(Expression left, Expression right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public int interpret(Map<String, Integer> context) {
    return left.interpret(context) - right.interpret(context);
  }
}
