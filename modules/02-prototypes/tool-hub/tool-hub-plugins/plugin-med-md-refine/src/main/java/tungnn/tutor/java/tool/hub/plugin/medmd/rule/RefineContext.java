package tungnn.tutor.java.tool.hub.plugin.medmd.rule;

import java.util.ArrayList;
import java.util.List;

public final class RefineContext {
  private final List<String> notes = new ArrayList<>();

  public void note(String message) {
    if (message != null && !message.isBlank()) {
      notes.add(message);
    }
  }

  List<String> drainNotes() {
    List<String> copy = List.copyOf(notes);
    notes.clear();
    return copy;
  }
}
