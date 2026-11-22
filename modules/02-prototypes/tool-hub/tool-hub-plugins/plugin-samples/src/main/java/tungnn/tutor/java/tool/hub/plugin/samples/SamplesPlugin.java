package tungnn.tutor.java.tool.hub.plugin.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import tungnn.tutor.java.tool.hub.api.*;

public class SamplesPlugin extends Plugin {

  @Extension
  public static class UuidTool implements ToolExtension {

    @Override
    public ToolDescriptor descriptor() {
      return new ToolDescriptor("uuid", "UUID Generator", "Sinh UUID ngẫu nhiên", "generator");
    }

    @Override
    public List<ParamSpec> params() {
      return List.of(
          new ParamSpec("count", "number", true, "Số lượng (1-100)", "5"),
          new ParamSpec("uppercase", "boolean", false, "Viết hoa"),
          new ParamSpec("prefix", "string", false, "Tiền tố mỗi dòng"));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) {
      Object rawCount = in.get("count");
      if (rawCount == null) {
        return ToolResult.fail("Thiếu tham số count");
      }
      int count;
      if (rawCount instanceof BigDecimal bd) {
        count = bd.intValue();
      } else if (rawCount instanceof Number num) {
        count = num.intValue();
      } else {
        try {
          count = Integer.parseInt(rawCount.toString().trim());
        } catch (NumberFormatException e) {
          return ToolResult.fail("count phải là số nguyên hợp lệ");
        }
      }

      if (count < 1 || count > 100) {
        return ToolResult.fail("count phải trong khoảng 1-100");
      }

      boolean upper = Boolean.TRUE.equals(in.get("uppercase"));
      String prefix = in.get("prefix") == null ? "" : in.get("prefix").toString();

      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < count; i++) {
        String u = UUID.randomUUID().toString();
        sb.append(prefix).append(upper ? u.toUpperCase(Locale.ROOT) : u).append('\n');
      }
      return ToolResult.ok(sb.toString().stripTrailing());
    }
  }

  @Extension
  public static class JsonFormatTool implements ToolExtension {

    @Override
    public ToolDescriptor descriptor() {
      return new ToolDescriptor("json-format", "JSON Formatter", "Format / minify JSON", "text");
    }

    @Override
    public List<ParamSpec> params() {
      return List.of(
          new ParamSpec("mode", "enum:pretty|minify", true, "Chế độ", "pretty"),
          new ParamSpec("json", "text", true, "JSON đầu vào"));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
      Object rawJson = in.get("json");
      if (rawJson == null) {
        throw new ToolException("Thiếu tham số json");
      }
      String json = rawJson.toString();
      Object rawMode = in.get("mode");
      boolean pretty = rawMode == null || "pretty".equals(rawMode.toString());
      return ToolResult.ok(JsonReformatter.reformat(json, pretty));
    }
  }

  @Extension
  public static class FileHashTool implements ToolExtension {

    @Override
    public ToolDescriptor descriptor() {
      return new ToolDescriptor("file-hash", "File Hash", "Tính checksum của file", "file");
    }

    @Override
    public List<ParamSpec> params() {
      return List.of(
          new ParamSpec("file", "file", true, "File cần tính hash"),
          new ParamSpec(
              "algorithm", "enum:ALL|MD5|SHA-1|SHA-256|SHA-512", true, "Thuật toán", "ALL"));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
      Object rawFile = in.get("file");
      if (!(rawFile instanceof ToolFile file)) {
        throw new ToolException("File không hợp lệ hoặc bị thiếu");
      }
      Object rawAlgo = in.get("algorithm");
      String algo = rawAlgo == null ? "ALL" : rawAlgo.toString();
      List<String> algos =
          "ALL".equals(algo) ? List.of("MD5", "SHA-1", "SHA-256", "SHA-512") : List.of(algo);

      Map<String, Object> out = new LinkedHashMap<>();
      out.put("File", file.filename());
      out.put("Size (bytes)", file.size());
      try {
        for (String a : algos) {
          out.put(a, HexFormat.of().formatHex(MessageDigest.getInstance(a).digest(file.content())));
        }
      } catch (NoSuchAlgorithmException e) {
        throw new ToolException("Thuật toán không hỗ trợ: " + e.getMessage());
      }
      return ToolResult.ok(out);
    }
  }

  @Extension
  public static class GzipTool implements ToolExtension {

    @Override
    public ToolDescriptor descriptor() {
      return new ToolDescriptor(
          "gzip", "Gzip Compress", "Nén file bằng gzip", "file", ToolLayout.FILE);
    }

    @Override
    public List<ParamSpec> params() {
      return List.of(new ParamSpec("file", "file", true, "File cần nén"));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
      Object rawFile = in.get("file");
      if (!(rawFile instanceof ToolFile file)) {
        throw new ToolException("File không hợp lệ hoặc bị thiếu");
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try (GZIPOutputStream gz = new GZIPOutputStream(bos)) {
        gz.write(file.content());
      } catch (IOException e) {
        throw new ToolException("Nén thất bại: " + e.getMessage());
      }

      byte[] compressed = bos.toByteArray();
      ctx.logger().info("gzip {}: {} -> {} bytes", file.filename(), file.size(), compressed.length);
      return ToolResult.ok(new ToolFile(file.filename() + ".gz", "application/gzip", compressed));
    }
  }

  @Extension
  public static class SysPropsTool implements ToolExtension {

    @Override
    public ToolDescriptor descriptor() {
      return new ToolDescriptor(
          "sys-props",
          "System Properties",
          "Liệt kê Java system properties của server",
          "system",
          ToolLayout.TABLE);
    }

    @Override
    public List<ParamSpec> params() {
      return List.of(
          new ParamSpec("filter", "string", false, "Lọc theo key (chứa chuỗi)", "java."));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) {
      String filter =
          in.get("filter") == null ? "" : in.get("filter").toString().toLowerCase(Locale.ROOT);
      var rows = new ArrayList<>();
      System.getProperties().stringPropertyNames().stream()
          .filter(k -> k.toLowerCase(Locale.ROOT).contains(filter))
          .sorted()
          .forEach(k -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("key", k);
            row.put("value", System.getProperty(k));
            rows.add(row);
          });
      return ToolResult.ok(rows);
    }
  }
}
