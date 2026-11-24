package tungnn.tutor.java.tool.hub.app.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.core.ToolRegistry;

@Component
public class ToolCatalog {
    private final ToolRegistry registry;

    public ToolCatalog(ToolRegistry registry) {
        this.registry = registry;
    }

    public static ToolView toView(ToolExtension ext) {
        List<FieldView> fields = FieldViewFactory.of(ext.params());
        return new ToolView(
                ext.descriptor(), LayoutResolver.resolve(ext.descriptor().layout(), fields), fields);
    }

    public Optional<ToolView> find(String id) {
        return registry.get(id).map(ToolCatalog::toView);
    }

    public Map<String, List<ToolView>> grouped() {
        return registry.all().stream()
                .map(ToolCatalog::toView)
                .sorted(Comparator.comparing(ToolView::name, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.groupingBy(ToolView::category, TreeMap::new, Collectors.toList()));
    }

    public int count() {
        return registry.all().size();
    }
}
