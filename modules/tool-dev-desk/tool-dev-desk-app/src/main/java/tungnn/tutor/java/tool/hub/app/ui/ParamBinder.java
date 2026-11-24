package tungnn.tutor.java.tool.hub.app.ui;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import tungnn.tutor.java.tool.hub.api.ToolFile;

public final class ParamBinder {
  private ParamBinder() {}

  public static BindResult bind(
      List<FieldView> fields, Map<String, String> params, Map<String, MultipartFile> files) {
    Map<String, Object> input = new LinkedHashMap<>();
    Map<String, String> errors = new LinkedHashMap<>();
    for (FieldView f : fields) {
      String name = f.name();
      if (f.widget() == Widget.CHECKBOX) {
        input.put(name, isTruthy(params.get(name)));
        continue;
      }
      if (f.widget() == Widget.FILE) {
        MultipartFile mf = files == null ? null : files.get(name);
        if (mf == null || mf.isEmpty()) {
          if (f.required()) errors.put(name, "Please choose a file");
          continue;
        }
        try {
          input.put(
              name, new ToolFile(mf.getOriginalFilename(), mf.getContentType(), mf.getBytes()));
        } catch (IOException e) {
          errors.put(name, "Unable to read file: " + e.getMessage());
        }
        continue;
      }
      String raw = params.get(name);
      if (raw == null || raw.isBlank()) {
        if (f.required()) errors.put(name, "Must not be empty");
        continue;
      }
      switch (f.widget()) {
        case NUMBER -> {
          try {
            input.put(name, new BigDecimal(raw.trim()));
          } catch (NumberFormatException e) {
            errors.put(name, "Must be a number");
          }
        }
        case SELECT -> {
          if (f.options().contains(raw)) input.put(name, raw);
          else errors.put(name, "Invalid value: " + raw);
        }
        default -> input.put(name, raw);
      }
    }
    return new BindResult(input, errors);
  }

  static boolean isTruthy(String v) {
    return v != null && (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("on"));
  }
}
