package tungnn.tutor.java.tool.hub.app.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolResult;
import tungnn.tutor.java.tool.hub.app.ui.ResultView.Kind;

@Component
public class ResultViewFactory {
  private final JsonMapper json;

  public ResultViewFactory(JsonMapper json) {
    this.json = json;
  }

  private static ResultView text(Kind kind, String text, String msg, long ms) {
    return new ResultView(kind, text, List.of(), List.of(), null, null, 0, msg, ms);
  }

  private static ResultView table(List<String> cols, List<List<String>> rows, String msg, long ms) {
    return new ResultView(Kind.TABLE, null, cols, rows, null, null, 0, msg, ms);
  }

  public ResultView create(
      ToolResult result, long elapsedMs, Function<ToolFile, String> fileStore) {
    Object data = result.data();
    String msg = result.message();
    if (data == null) return text(Kind.EMPTY, "", msg, elapsedMs);
    if (data instanceof CharSequence || data instanceof Number || data instanceof Boolean) {
      return text(Kind.TEXT, data.toString(), msg, elapsedMs);
    }
    if (data instanceof ToolFile f) {
      String token = fileStore.apply(f);
      return new ResultView(
          Kind.FILE, null, List.of(), List.of(), token, f.filename(), f.size(), msg, elapsedMs);
    }
    if (data instanceof Map<?, ?> map) {
      List<List<String>> rows = new ArrayList<>();
      map.forEach((k, v) -> rows.add(List.of(String.valueOf(k), cell(v))));
      return table(List.of("Key", "Value"), rows, msg, elapsedMs);
    }
    if (data instanceof Collection<?> items) {
      if (!items.isEmpty() && items.stream().allMatch(i -> i instanceof Map<?, ?>)) {
        Set<String> cols = new LinkedHashSet<>();
        items.forEach(i -> ((Map<?, ?>) i).keySet().forEach(k -> cols.add(String.valueOf(k))));
        List<List<String>> rows = new ArrayList<>();
        for (Object i : items) {
          Map<?, ?> m = (Map<?, ?>) i;
          List<String> row = new ArrayList<>();
          for (String c : cols) row.add(cell(m.get(c)));
          rows.add(row);
        }
        return table(List.copyOf(cols), rows, msg, elapsedMs);
      }
      List<List<String>> rows = items.stream().map(i -> List.of(cell(i))).toList();
      return table(List.of("Value"), rows, msg, elapsedMs);
    }
    return text(Kind.JSON, toJson(data, true), msg, elapsedMs);
  }

  private String cell(Object v) {
    if (v == null) return "";
    if (v instanceof CharSequence || v instanceof Number || v instanceof Boolean) {
      return v.toString();
    }
    return toJson(v, false);
  }

  private String toJson(Object v, boolean pretty) {
    try {
      return pretty
          ? json.writerWithDefaultPrettyPrinter().writeValueAsString(v)
          : json.writeValueAsString(v);
    } catch (RuntimeException e) {
      return String.valueOf(v);
    }
  }
}
