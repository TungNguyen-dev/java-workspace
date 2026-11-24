package tungnn.tutor.java.tool.hub.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolResult;
import tungnn.tutor.java.tool.hub.app.ui.BindResult;
import tungnn.tutor.java.tool.hub.app.ui.FieldView;
import tungnn.tutor.java.tool.hub.app.ui.ParamBinder;
import tungnn.tutor.java.tool.hub.app.ui.ResultView;
import tungnn.tutor.java.tool.hub.app.ui.ResultViewFactory;
import tungnn.tutor.java.tool.hub.app.ui.ToolCatalog;
import tungnn.tutor.java.tool.hub.app.ui.ToolView;
import tungnn.tutor.java.tool.hub.core.ToolEntry;
import tungnn.tutor.java.tool.hub.core.ToolRegistry;

@Controller
public class ToolController {
    private static final Logger log = LoggerFactory.getLogger(ToolController.class);
    private final ToolCatalog catalog;
    private final ToolRegistry registry;
    private final ResultViewFactory resultViews;
    private final DownloadStore downloads;

    public ToolController(
            ToolCatalog catalog, ToolRegistry registry, ResultViewFactory resultViews, DownloadStore downloads) {
        this.catalog = catalog;
        this.registry = registry;
        this.resultViews = resultViews;
        this.downloads = downloads;
    }

    private static String notFound(String id, RedirectAttributes ra) {
        ra.addFlashAttribute("flash", "Tool '" + id + "' does not exist or the plugin has been removed.");
        return "redirect:/";
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
        Map<String, List<ToolView>> grouped = catalog.grouped();
        model.addAttribute("catalog", grouped);
        model.addAttribute("toolCount", grouped.values().stream().mapToInt(List::size).sum());
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
        Optional<ToolEntry> entry = registry.entry(id);
        Optional<ToolView> found = entry.flatMap(e -> catalog.view(e.extension()));
        if (found.isEmpty()) return notFound(id, ra);
        ToolView tool = found.get();
        Map<String, MultipartFile> files =
                request instanceof MultipartHttpServletRequest mr ? mr.getFileMap() : Map.of();
        BindResult bound = ParamBinder.bind(tool.fields(), params, files);
        if (bound.hasErrors()) {
            return render(model, tool, params, bound.errors(), null, "Invalid input");
        }
        long start = System.nanoTime();
        try {
            ToolResult result = entry.get().extension().execute(entry.get().context(), bound.input());
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (result == null) return render(model, tool, params, Map.of(), null, "The tool returned no result");
            if (!result.success()) {
                String msg = result.message() == null ? "Tool reported an error" : result.message();
                return render(model, tool, params, Map.of(), null, msg);
            }
            ResultView view = resultViews.create(result, ms, file -> downloads.put(request.getSession().getId(), file));
            return render(model, tool, params, Map.of(), view, null);
        } catch (ToolException e) {
            log.warn("Tool {} reported an error", id, e);
            return render(model, tool, params, Map.of(), null, e.getMessage());
        } catch (RuntimeException e) {
            log.error("Tool {} failed while running", id, e);
            return render(model, tool, params, Map.of(), null, "Unexpected error: " + e);
        } catch (LinkageError e) {
            log.error("Tool {} has a library compatibility error", id, e);
            return render(model, tool, params, Map.of(), null, "Plugin library compatibility error: " + e);
        }
    }

    @GetMapping("/tools/{id}/download/{token}")
    public ResponseEntity<byte[]> download(@PathVariable String id, @PathVariable String token, HttpSession session) {
        ToolFile file = downloads.get(session.getId(), token).orElse(null);
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
