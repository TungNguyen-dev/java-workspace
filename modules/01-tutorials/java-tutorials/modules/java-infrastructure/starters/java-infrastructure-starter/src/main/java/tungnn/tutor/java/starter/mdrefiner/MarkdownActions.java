package tungnn.tutor.java.starter.mdrefiner;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.CodeBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.ListItem;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.OrderedList;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownActions {

  private MarkdownActions() {}

  /** Action tổng quát: tự xử lý toàn bộ danh sách block. */
  public static MarkdownAction edit(Function<List<MarkdownBlock>, List<MarkdownBlock>> fn) {
    return doc -> new MarkdownDocument(fn.apply(new ArrayList<>(doc.blocks())));
  }

  /** Gộp `count` block liên tiếp từ vị trí `from` (0-based) thành 1 Paragraph. */
  public static MarkdownAction mergeToParagraph(int from, int count, String separator) {
    return edit(blocks -> {
      checkRange(blocks, from, count);
      var text = new StringBuilder();
      for (int i = from; i < from + count; i++) {
        var t = textOf(blocks.get(i));
        if (t.isEmpty()) continue;
        if (!text.isEmpty()) text.append(separator);
        text.append(t);
      }
      blocks.subList(from, from + count).clear();
      blocks.add(from, new Paragraph(text.toString()));
      return blocks;
    });
  }

  /** Gộp `count` block liên tiếp từ vị trí `from` thành 1 Heading. */
  public static MarkdownAction mergeToHeading(int from, int count, int level, String separator) {
    return doc -> {
      var merged = mergeToParagraph(from, count, separator).apply(doc);
      return toHeading(from, level).apply(merged);
    };
  }

  /** Chuyển block tại `index` thành Heading (lấy text của block đó). */
  public static MarkdownAction toHeading(int index, int level) {
    return edit(blocks -> {
      checkRange(blocks, index, 1);
      blocks.set(index, new Heading(level, textOf(blocks.get(index))));
      return blocks;
    });
  }

  /** Xóa các block thỏa điều kiện (chỉ ở cấp document, không đệ quy vào list). */
  public static MarkdownAction removeIf(Predicate<MarkdownBlock> predicate) {
    return edit(blocks -> {
      blocks.removeIf(predicate);
      return blocks;
    });
  }

  /** Thay text theo regex trong Heading/Paragraph (đệ quy vào OrderedList, bỏ qua CodeBlock). */
  public static MarkdownAction replaceText(String regex, String replacement) {
    var pattern = Pattern.compile(regex);
    return doc ->
        new MarkdownDocument(
            doc.blocks().stream()
                .map(b -> mapText(b, s -> pattern.matcher(s).replaceAll(replacement)))
                .toList());
  }

  public static MarkdownAction normalize(MarkdownRefiner refiner) {
    return doc ->
        new MarkdownDocument(doc.blocks().stream().map(b -> refineBlock(b, refiner)).toList());
  }

  private static MarkdownBlock refineBlock(MarkdownBlock block, MarkdownRefiner r) {
    return switch (block) {
      case Heading h -> new Heading(h.level(), r.refine("Heading", h.text()));
      case Paragraph p -> new Paragraph(r.refine("Paragraph", p.text()));
      case CodeBlock c -> c;
      case OrderedList l ->
          new OrderedList(
              l.startNumber(),
              l.items().stream()
                  .map(item ->
                      new ListItem(item.blocks().stream().map(b -> refineBlock(b, r)).toList()))
                  .toList());
    };
  }

  /**
   * Paragraph thỏa pattern thì chuyển thành Heading (chỉ ở cấp document). Text được gộp mọi khoảng
   * trắng liền nhau thành 1 dấu cách.
   */
  public static MarkdownAction headingIf(Pattern pattern, int level) {
    if (level < 1 || level > 6) {
      throw new IllegalArgumentException("level phải 1..6");
    }
    return edit(blocks -> {
      for (int i = 0; i < blocks.size(); i++) {
        if (blocks.get(i) instanceof Paragraph(String text)) {
          var raw = Normalizer.normalize(text, Normalizer.Form.NFC).strip();
          if (pattern.matcher(raw).find()) {
            blocks.set(i, new Heading(level, raw.replaceAll("\\s+", " ")));
          }
        }
      }
      return blocks;
    });
  }

  /** Paragraph bắt đầu bằng "Mục", có ít nhất 4 khoảng trắng liền nhau (hoặc tab) ở giữa. */
  public static MarkdownAction muc(int level) {
    return headingIf(Pattern.compile("(?s)^Mục\\s.*?(\\s{4,}|\\t)\\S"), level);
  }

  /**
   * Với mỗi Paragraph thỏa `pattern`, chuyển Paragraph liền trước nó (bỏ qua paragraph rỗng) thành
   * Heading. Chỉ ở cấp document; block liền trước không phải Paragraph thì giữ nguyên.
   */
  public static MarkdownAction headingBefore(Pattern pattern, int level) {
    if (level < 1 || level > 6) {
      throw new IllegalArgumentException("level phải 1..6");
    }
    return edit(blocks -> {
      for (int i = 1; i < blocks.size(); i++) {
        if (!(blocks.get(i) instanceof Paragraph(String text))
            || !pattern.matcher(Normalizer.normalize(text, Normalizer.Form.NFC).strip()).find()) {
          continue;
        }
        int prev = i - 1;
        while (prev >= 0 && blocks.get(prev) instanceof Paragraph(String t) && t.isBlank()) prev--;
        if (prev >= 0 && blocks.get(prev) instanceof Paragraph(String prevText)) {
          var title =
              Normalizer.normalize(prevText, Normalizer.Form.NFC).strip().replaceAll("\\s+", " ");
          blocks.set(prev, new Heading(level, title));
        }
      }
      return blocks;
    });
  }

  /** Paragraph liền trước paragraph "【Thành phần】..." (tên bài thuốc) thành Heading. */
  public static MarkdownAction thanhPhan(int level) {
    return headingBefore(Pattern.compile("^【\\s*Thành phần\\s*】"), level);
  }

  /** 【title】content hoặc [title]content ở đầu paragraph. */
  private static final Pattern BRACKET_TITLE =
      Pattern.compile("(?s)^(?:【([^】]+)】|\\[([^\\]]+)\\](?!\\())\\s*(.*)$");

  /**
   * Paragraph bắt đầu bằng 【title】content hoặc [title]content thì tách thành Heading(title) +
   * Paragraph(content). Chỉ ở cấp document. Nếu content rỗng thì chỉ tạo Heading.
   */
  public static MarkdownAction bracketHeading(int level) {
    if (level < 1 || level > 6) {
      throw new IllegalArgumentException("level phải 1..6");
    }
    return edit(blocks -> {
      var out = new ArrayList<MarkdownBlock>();
      for (var block : blocks) {
        if (block instanceof Paragraph(String text)) {
          var m = BRACKET_TITLE.matcher(text.strip());
          if (m.matches()) {
            var title =
                (m.group(1) != null ? m.group(1) : m.group(2)).strip().replaceAll("\\s+", " ");
            var content = m.group(3).strip();
            if (!title.isEmpty()) {
              out.add(new Heading(level, title));
              if (!content.isEmpty()) out.add(new Paragraph(content));
              continue;
            }
          }
        }
        out.add(block);
      }
      return out;
    });
  }

  // ---------- helpers ----------

  public static String textOf(MarkdownBlock block) {
    return switch (block) {
      case Heading h -> h.text().strip();
      case Paragraph p -> p.text().strip();
      case CodeBlock c -> c.code() == null ? "" : c.code().strip();
      case OrderedList l -> l.toMarkdown().strip();
    };
  }

  private static MarkdownBlock mapText(MarkdownBlock block, UnaryOperator<String> fn) {
    return switch (block) {
      case Heading h -> new Heading(h.level(), fn.apply(h.text()));
      case Paragraph p -> new Paragraph(fn.apply(p.text()));
      case CodeBlock c -> c;
      case OrderedList l ->
          new OrderedList(
              l.startNumber(),
              l.items().stream()
                  .map(item ->
                      new ListItem(item.blocks().stream().map(b -> mapText(b, fn)).toList()))
                  .toList());
    };
  }

  private static void checkRange(List<MarkdownBlock> blocks, int from, int count) {
    if (from < 0 || count < 1 || from + count > blocks.size()) {
      throw new IllegalArgumentException(
          "Range không hợp lệ: from=%d, count=%d, size=%d".formatted(from, count, blocks.size()));
    }
  }
}
