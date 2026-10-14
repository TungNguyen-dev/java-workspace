void main() {
  TextEditor editor = new TextEditor();
  History history = new History();

  // 1. Type something and save
  editor.type("Version 1: The beginning.");
  history.push(editor.save());
  System.out.println("Current: " + editor.getContent());

  // 2. Type something else and save
  editor.type("Version 2: Adding more content.");
  history.push(editor.save());
  System.out.println("Current: " + editor.getContent());

  // 3. Make a mistake
  editor.type("Version 3: This was a mistake!");
  System.out.println("Oops: " + editor.getContent());

  // 4. Undo!
  editor.restore(history.pop()); // Back to V2
  System.out.println("Restored to: " + editor.getContent());

  editor.restore(history.pop()); // Back to V1
  System.out.println("Restored to: " + editor.getContent());
}

// The Originator
class TextEditor {
  private String content;

  public void type(String text) {
    this.content = text;
  }

  public String getContent() {
    return content;
  }

  // Creates a new snapshot
  public Memento save() {
    return new Memento(content);
  }

  // Restores state from a snapshot
  public void restore(Memento memento) {
    this.content = memento.getState();
  }

  // The Memento - Nested inner class
  public static class Memento {
    private final String state;

    private Memento(String state) {
      this.state = state;
    }

    private String getState() {
      return state;
    }
  }
}

// The Caretaker
class History {
  private Stack<TextEditor.Memento> history = new Stack<>();

  public void push(TextEditor.Memento memento) {
    history.push(memento);
  }

  public TextEditor.Memento pop() {
    if (!history.isEmpty()) {
      return history.pop();
    }
    return null;
  }
}
