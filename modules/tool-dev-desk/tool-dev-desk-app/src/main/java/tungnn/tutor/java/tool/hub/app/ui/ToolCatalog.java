package tungnn.tutor.java.tool.hub.app.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.core.ToolRegistry;

@Component
public class ToolCatalog {
    private static final Logger log = LoggerFactory.getLogger(ToolCatalog.class);
    private final ToolRegistry registry;
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();

    private record Cached(ToolExtension extension, ToolView view) {}

    public ToolCatalog(ToolRegistry registry) {
        this.registry = registry;
    }

    public static ToolView toView(ToolExtension ext) {
        List<FieldView> fields = FieldViewFactory.of(ext.params());
        return new ToolView(
                ext.descriptor(), LayoutResolver.resolve(ext.descriptor().layout(), fields), fields);
    }

    public Optional<ToolView> view(ToolExtension ext) {
        String id;
        try {
            id = ext.descriptor().id();
        } catch (Throwable t) {
            log.error("Unable to read the descriptor of tool {}", ext.getClass().getName(), t);
            return Optional.empty();
        }
        Cached cached = cache.get(id);
        if (cached != null && cached.extension() == ext) return Optional.ofNullable(cached.view());
        ToolView view = null;
        try {
            view = toView(ext);
        } catch (Throwable t) {
            log.error("Tool {} cannot be displayed and is hidden from the UI", id, t);
        }
        cache.put(id, new Cached(ext, view));
        return Optional.ofNullable(view);
    }

    public Optional<ToolView> find(String id) {
        return registry.entry(id).flatMap(e -> view(e.extension()));
    }

    public Map<String, List<ToolView>> grouped() {
        List<ToolView> views = registry.all().stream()
                .map(this::view)
                .flatMap(Optional::stream)
                .sorted(Comparator.comparing(ToolView::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
        cache.values().removeIf(c -> !registry.all().contains(c.extension()));
        return views.stream().collect(Collectors.groupingBy(ToolView::category, TreeMap::new, Collectors.toList()));
    }
}
