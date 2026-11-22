package tungnn.tutor.java.tool.hub.plugin.medmd.rule;

import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public abstract class BlockRefineRule implements RefineRule {
  @Override
  public final MarkdownDocument apply(MarkdownDocument document, RefineContext context) {
    return new MarkdownDocument(refineAll(document.blocks(), context));
  }

  protected abstract List<MarkdownBlock> refine(MarkdownBlock block, RefineContext context);

  private List<MarkdownBlock> refineAll(List<MarkdownBlock> blocks, RefineContext context) {
    List<MarkdownBlock> out = new ArrayList<>(blocks.size());
    for (MarkdownBlock block : blocks) {
      MarkdownBlock current = block;
      if (block instanceof MarkdownBlock.OrderedList list) {
        List<MarkdownBlock.ListItem> items = new ArrayList<>(list.items().size());
        for (MarkdownBlock.ListItem item : list.items()) {
          items.add(new MarkdownBlock.ListItem(refineAll(item.blocks(), context)));
        }
        current = new MarkdownBlock.OrderedList(list.startNumber(), items);
      }
      List<MarkdownBlock> replaced = refine(current, context);
      if (replaced != null) {
        out.addAll(replaced);
      }
    }
    return out;
  }
}
