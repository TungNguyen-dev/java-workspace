void main() {
  // 1. Create the Mediator
  AuthenticationDialog dialog = new AuthenticationDialog();

  // 2. Create components and link them to the mediator
  ConcreteButton loginBtn = new ConcreteButton(dialog);
  ConcreteCheckbox rememberMe = new ConcreteCheckbox(dialog);

  // 3. Let the mediator know which specific objects it's managing
  dialog.setSubmitButton(loginBtn);
  dialog.setCheckbox(rememberMe);

  // 4. Interaction: Components only talk to the mediator
  rememberMe.check();
  loginBtn.click();
}

// The Mediator interface
interface Mediator {
  void notify(Component sender, String event);
}

// The Concrete Mediator
class AuthenticationDialog implements Mediator {
  private ConcreteButton submitButton;
  private ConcreteCheckbox stayLoggedIn;

  // The mediator links all components together
  public void setSubmitButton(ConcreteButton button) {
    this.submitButton = button;
  }

  public void setCheckbox(ConcreteCheckbox checkbox) {
    this.stayLoggedIn = checkbox;
  }

  @Override
  public void notify(Component sender, String event) {
    if (sender instanceof ConcreteCheckbox && event.equals("check")) {
      if (stayLoggedIn.isChecked()) {
        System.out.println("Mediator: Checkbox checked. Enabling specialized login features.");
      }
    }

    if (sender instanceof ConcreteButton && event.equals("click")) {
      System.out.println("Mediator: Button clicked. Validating form and submitting...");
    }
  }
}

// Base component class
abstract class Component {
  protected Mediator mediator;

  public Component(Mediator mediator) {
    this.mediator = mediator;
  }
}

// Concrete Components
class ConcreteButton extends Component {
  public ConcreteButton(Mediator mediator) {
    super(mediator);
  }

  public void click() {
    System.out.println("Button: Clicked.");
    mediator.notify(this, "click");
  }

  public void setEnabled(boolean enabled) {
    System.out.println("Button: Enabled status set to " + enabled);
  }
}

class ConcreteCheckbox extends Component {
  private boolean checked;

  public ConcreteCheckbox(Mediator mediator) {
    super(mediator);
  }

  public void check() {
    this.checked = !this.checked;
    System.out.println("Checkbox: Checked = " + checked);
    mediator.notify(this, "check");
  }

  public boolean isChecked() {
    return checked;
  }
}
