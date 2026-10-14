void main() {
  // 1. Create the handlers
  SupportHandler bot = new BotHandler();
  SupportHandler operator = new OperatorHandler();
  SupportHandler engineer = new EngineerHandler();

  // 2. Chain them together: Bot -> Operator -> Engineer
  bot.setNext(operator);
  operator.setNext(engineer);

  // 3. Send different requests to the FIRST link in the chain
  System.out.println("--- Scenario 1 ---");
  bot.handleRequest("BASIC");

  System.out.println("\n--- Scenario 2 ---");
  bot.handleRequest("INTERMEDIATE");

  System.out.println("\n--- Scenario 3 ---");
  bot.handleRequest("CRITICAL");

  System.out.println("\n--- Scenario 4 (Unhandled) ---");
  bot.handleRequest("ALIEN_INVASION");
}

public interface SupportHandler {
  void setNext(SupportHandler nextHandler);

  void handleRequest(String issueSeverity);
}

public abstract class BaseSupportHandler implements SupportHandler {
  private SupportHandler next;

  @Override
  public void setNext(SupportHandler nextHandler) {
    this.next = nextHandler;
  }

  protected void passToNext(String issueSeverity) {
    if (next != null) {
      next.handleRequest(issueSeverity);
    } else {
      System.out.println("End of chain: No one could handle the issue: " + issueSeverity);
    }
  }
}

// Level 1: Automatic Bot
class BotHandler extends BaseSupportHandler {
  @Override
  public void handleRequest(String issueSeverity) {
    if (issueSeverity.equals("BASIC")) {
      System.out.println("Bot: I've reset your password. Issue resolved.");
    } else {
      System.out.println("Bot: This looks complex. Passing to an Operator...");
      passToNext(issueSeverity);
    }
  }
}

// Level 2: Human Operator
class OperatorHandler extends BaseSupportHandler {
  @Override
  public void handleRequest(String issueSeverity) {
    if (issueSeverity.equals("INTERMEDIATE")) {
      System.out.println("Operator: I've updated your billing info. Issue resolved.");
    } else {
      System.out.println("Operator: I can't fix this. Passing to an Engineer...");
      passToNext(issueSeverity);
    }
  }
}

// Level 3: Specialized Engineer
class EngineerHandler extends BaseSupportHandler {
  @Override
  public void handleRequest(String issueSeverity) {
    if (issueSeverity.equals("CRITICAL")) {
      System.out.println("Engineer: I've patched the server kernel. Issue resolved.");
    } else {
      passToNext(issueSeverity);
    }
  }
}
