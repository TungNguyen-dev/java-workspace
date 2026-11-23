package tungnn.tutor.java.tool.hub.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolResult;
import tungnn.tutor.java.tool.hub.app.ui.BindResult;
import tungnn.tutor.java.tool.hub.app.ui.FieldView;
import tungnn.tutor.java.tool.hub.app.ui.ParamBinder;
import tungnn.tutor.java.tool.hub.app.ui.ResultView;
import tungnn.tutor.java.tool.hub.app.ui.ResultViewFactory;
import tungnn.tutor.java.tool.hub.app.ui.ToolCatalog;
import tungnn.tutor.java.tool.hub.app.ui.ToolView;
import tungnn.tutor.java.tool.hub.core.ToolRegistry;

@Controller
public class ToolController {
  private static final Logger log = LoggerFactory.getLogger(ToolController.class);
  private static final String DOWNLOADS_ATTR = "hub.downloads";
  private static final int MAX_DOWNLOADS_PER_SESSION = 10;
  private final ToolCatalog catalog;
  private final ToolRegistry registry;
  private final ResultViewFactory resultViews;

  public ToolController(ToolCatalog catalog, ToolRegistry registry, ResultViewFactory resultViews) {
    this.catalog = catalog;
    this.registry = registry;
    this.resultViews = resultViews;
  }

  private static String notFound(String id, RedirectAttributes ra) {
    ra.addFlashAttribute("flash", "Tool '" + id + "' không tồn tại hoặc plugin đã bị gỡ.");
    return "redirect:/";
  }

  private static String storeDownload(HttpSession session, ToolFile file) {
    String token = UUID.randomUUID().toString();
    Map<String, ToolFile> store = downloads(session);
    synchronized (store) {
      store.put(token, file);
    }
    return token;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, ToolFile> downloads(HttpSession session) {
    synchronized (session) {
      Object existing = session.getAttribute(DOWNLOADS_ATTR);
      if (existing != null) return (Map<String, ToolFile>) existing;
      Map<String, ToolFile> store =
          new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ToolFile> eldest) {
              return size() > MAX_DOWNLOADS_PER_SESSION;
            }
          };
      session.setAttribute(DOWNLOADS_ATTR, store);
      return store;
    }
  }

  private static MediaType safeMediaType(String type) {
    try {
      return MediaType.parseMediaType(type);
    } catch (RuntimeException e) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  @ModelAttribute
  void common(Model model) {
    model.addAttribute("catalog", catalog.grouped());
    model.addAttribute("toolCount", catalog.count());
  }

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("/tools/{id}")
  public String show(@PathVariable String id, Model model, RedirectAttributes ra) {
    Optional<ToolView> tool = catalog.find(id);
    if (tool.isEmpty()) return notFound(id, ra);
    Map<String, String> defaults = new LinkedHashMap<>();
    for (FieldView f : tool.get().fields()) {
      if (f.defaultValue() != null) defaults.put(f.name(), f.defaultValue());
    }
    return render(model, tool.get(), defaults, Map.of(), null, null);
  }

  @PostMapping("/tools/{id}")
  public String run(
      @PathVariable String id,
      @RequestParam Map<String, String> params,
      HttpServletRequest request,
      Model model,
      RedirectAttributes ra) {
    Optional<ToolView> found = catalog.find(id);
    Optional<ToolExtension> ext = registry.get(id);
    if (found.isEmpty() || ext.isEmpty()) return notFound(id, ra);
    ToolView tool = found.get();
    Map<String, MultipartFile> files =
        request instanceof MultipartHttpServletRequest mr ? mr.getFileMap() : Map.of();
    BindResult bound = ParamBinder.bind(tool.fields(), params, files);
    if (bound.hasErrors()) {
      return render(model, tool, params, bound.errors(), null, "Dữ liệu nhập chưa hợp lệ");
    }
    ToolContext ctx = registry.context(id).orElse(null);
    long start = System.nanoTime();
    try {
      ToolResult result = ext.get().execute(ctx, bound.input());
      long ms = (System.nanoTime() - start) / 1_000_000;
      if (result == null)
        return render(model, tool, params, Map.of(), null, "Tool không trả về kết quả");
      if (!result.success()) {
        String msg = result.message() == null ? "Tool báo lỗi" : result.message();
        return render(model, tool, params, Map.of(), null, msg);
      }
      ResultView view =
          resultViews.create(result, ms, file -> storeDownload(request.getSession(), file));
      return render(model, tool, params, Map.of(), view, null);
    } catch (ToolException e) {
      return render(model, tool, params, Map.of(), null, e.getMessage());
    } catch (RuntimeException e) {
      log.error("Tool {} lỗi khi chạy", id, e);
      return render(model, tool, params, Map.of(), null, "Lỗi không mong muốn: " + e);
    } catch (LinkageError e) {
      log.error("Tool {} lỗi tương thích thư viện", id, e);
      return render(model, tool, params, Map.of(), null, "Plugin lỗi tương thích thư viện: " + e);
    }
  }

  @GetMapping("/tools/{id}/download/{token}")
  public ResponseEntity<byte[]> download(
      @PathVariable String id, @PathVariable String token, HttpSession session) {
    ToolFile file = downloads(session).get(token);
    if (file == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(file.filename(), StandardCharsets.UTF_8)
                .build()
                .toString())
        .contentType(safeMediaType(file.contentType()))
        .contentLength(file.size())
        .body(file.content());
  }

  private String render(
      Model model,
      ToolView tool,
      Map<String, String> values,
      Map<String, String> errors,
      ResultView result,
      String error) {
    model.addAttribute("tool", tool);
    model.addAttribute("values", values);
    model.addAttribute("errors", errors);
    model.addAttribute("result", result);
    model.addAttribute("error", error);
    return tool.template();
  }
}
