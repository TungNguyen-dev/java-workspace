package tungnn.tutor.java.infra.document.converter;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.ast.Code;
import com.vladsch.flexmark.ast.Emphasis;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.HardLineBreak;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.ListBlock;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.OrderedList;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.StrongEmphasis;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ast.ThematicBreak;
import com.vladsch.flexmark.ext.tables.TableBlock;
import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

/** Converts Markdown to Word (.docx) with global styling options. */
public final class MarkdownDocxConverterUtils {

  private MarkdownDocxConverterUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  // ==========================================================================
  // Options
  // ==========================================================================

  /** Numbering scheme prepended to a heading. Counters restart under each parent heading. */
  public enum Numbering {
    NONE,
    UPPER_ROMAN, // I. II. III.
    DECIMAL, // 1. 2. 3.
    LOWER_LETTER // a) b) c)
  }

  /** Format of a single heading level. Sizes in points, colour as RRGGBB hex. */
  public record HeadingStyle(
      int sizePt,
      String colorHex,
      int spaceBeforePt,
      int spaceAfterPt,
      boolean italic,
      boolean upperCase,
      boolean centered,
      Numbering numbering) {}

  public record Options(
      String bodyFont,
      String codeFont,
      int bodySizePt,
      double lineSpacing,
      List<HeadingStyle> headings) {

    public Options {
      if (headings == null || headings.size() != 6) {
        throw new IllegalArgumentException("Exactly 6 heading styles are required");
      }
    }

    public static Options defaults() {
      return new Options(
          "Times New Roman",
          "Courier New",
          13,
          1.5, // Default line spacing
          List.of(
              // H1: no numbering, UPPERCASE, centered
              new HeadingStyle(13, "1F3864", 18, 6, false, true, true, Numbering.NONE),
              // H2: UPPERCASE, I. II. III.
              new HeadingStyle(13, "2F5496", 14, 6, false, true, false, Numbering.UPPER_ROMAN),
              // H3: UPPERCASE, 1. 2. 3., centered
              new HeadingStyle(13, "2F5496", 12, 4, false, true, true, Numbering.DECIMAL),
              // H4: a) b) c)
              new HeadingStyle(13, "1F3864", 10, 4, false, false, false, Numbering.LOWER_LETTER),
              // H5: centered, no numbering
              new HeadingStyle(13, "1F3864", 8, 3, false, false, true, Numbering.NONE),
              // H6: unchanged
              new HeadingStyle(13, "595959", 8, 3, true, false, false, Numbering.NONE)));
    }
  }

  // ==========================================================================
  // Public API
  // ==========================================================================

  public static void convertMarkdownToDocx(Path mdPath, Path docxPath) throws IOException {
    convertMarkdownToDocx(mdPath, docxPath, Options.defaults());
  }

  public static void convertMarkdownToDocx(Path mdPath, Path docxPath, Options options)
      throws IOException {
    String markdown = Files.readString(mdPath, StandardCharsets.UTF_8);
    try (XWPFDocument document = toDocument(markdown, options);
        OutputStream out = Files.newOutputStream(docxPath)) {
      document.write(out);
    }
  }

  public static XWPFDocument toDocument(String markdown, Options options) {
    XWPFDocument document = new XWPFDocument();
    ensureHeadingStyles(document, options);

    MutableDataSet parserOptions = new MutableDataSet();
    parserOptions.set(Parser.EXTENSIONS, List.of(TablesExtension.create()));
    Node root = Parser.builder(parserOptions).build().parse(markdown == null ? "" : markdown);

    new Renderer(document, options).renderChildren(root, 0, false);
    return document;
  }

  // ==========================================================================
  // Heading styles
  // ==========================================================================

  private static void ensureHeadingStyles(XWPFDocument document, Options options) {
    XWPFStyles styles = document.getStyles();
    if (styles == null) {
      styles = document.createStyles();
    }

    for (int level = 1; level <= 6; level++) {
      String styleId = "Heading" + level;
      if (styles.styleExist(styleId)) {
        continue;
      }
      HeadingStyle hs = options.headings().get(level - 1);

      CTStyle ct = CTStyle.Factory.newInstance();
      ct.setStyleId(styleId);
      ct.setType(STStyleType.PARAGRAPH);
      ct.addNewName().setVal("heading " + level);
      ct.addNewUiPriority().setVal(BigInteger.valueOf(9));
      ct.addNewQFormat();

      CTPPrGeneral ppr = ct.addNewPPr();
      ppr.addNewKeepNext();
      ppr.addNewKeepLines();
      var spacing = ppr.addNewSpacing();
      spacing.setBefore(BigInteger.valueOf(hs.spaceBeforePt() * 20L));
      spacing.setAfter(BigInteger.valueOf(hs.spaceAfterPt() * 20L));
      if (hs.centered()) {
        ppr.addNewJc().setVal(STJc.CENTER);
      }
      ppr.addNewOutlineLvl().setVal(BigInteger.valueOf(level - 1));

      CTRPr rpr = ct.addNewRPr();
      CTFonts fonts = rpr.addNewRFonts();
      fonts.setAscii(options.bodyFont());
      fonts.setHAnsi(options.bodyFont());
      fonts.setCs(options.bodyFont());
      fonts.setEastAsia(options.bodyFont());
      rpr.addNewB();
      rpr.addNewBCs();
      if (hs.italic()) {
        rpr.addNewI();
        rpr.addNewICs();
      }
      if (hs.upperCase()) {
        rpr.addNewCaps();
      }
      rpr.addNewColor().setVal(hs.colorHex());
      rpr.addNewSz().setVal(BigInteger.valueOf(hs.sizePt() * 2L));
      rpr.addNewSzCs().setVal(BigInteger.valueOf(hs.sizePt() * 2L));

      styles.addStyle(new XWPFStyle(ct, styles));
    }
  }

  // ==========================================================================
  // Rendering
  // ==========================================================================

  private record Ctx(boolean bold, boolean italic, boolean bodyFont) {
    Ctx withBold() {
      return new Ctx(true, italic, bodyFont);
    }

    Ctx withItalic() {
      return new Ctx(bold, true, bodyFont);
    }
  }

  private static final class Renderer {
    private final XWPFDocument doc;
    private final Options opt;

    Renderer(XWPFDocument doc, Options opt) {
      this.doc = doc;
      this.opt = opt;
    }

    private final int[] headingCounters = new int[7];

    private String nextHeadingLabel(int level) {
      for (int i = level + 1; i < headingCounters.length; i++) {
        headingCounters[i] = 0;
      }

      Numbering numbering = opt.headings().get(level - 1).numbering();
      if (numbering == Numbering.NONE) {
        return "";
      }

      int n = ++headingCounters[level];
      return switch (numbering) {
        case UPPER_ROMAN -> toRoman(n) + ".";
        case DECIMAL -> n + ".";
        case LOWER_LETTER -> toLetters(n) + ")";
        case NONE -> "";
      };
    }

    private static String toRoman(int number) {
      int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
      String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < values.length; i++) {
        while (number >= values[i]) {
          sb.append(symbols[i]);
          number -= values[i];
        }
      }
      return sb.toString();
    }

    private static String toLetters(int number) {
      StringBuilder sb = new StringBuilder();
      while (number > 0) {
        number--;
        sb.insert(0, (char) ('a' + number % 26));
        number /= 26;
      }
      return sb.toString();
    }

    void renderChildren(Node parent, int indent, boolean quote) {
      for (Node n = parent.getFirstChild(); n != null; n = n.getNext()) {
        renderBlock(n, indent, quote);
      }
    }

    void renderBlock(Node node, int indent, boolean quote) {
      switch (node) {
        case Heading h -> {
          XWPFParagraph p = doc.createParagraph();
          int level = Math.clamp(h.getLevel(), 1, 6);
          p.setStyle("Heading" + level);
          String label = nextHeadingLabel(level);
          if (!label.isEmpty()) {
            p.createRun().setText(label + " ");
          }
          writeInline(h, p, new Ctx(false, false, false));
        }
        case Paragraph para -> {
          XWPFParagraph p = newBody(indent, quote);
          writeInline(para, p, new Ctx(false, false, true));
        }
        case ListBlock list -> renderList(list, indent, quote);
        case FencedCodeBlock code -> renderCode(code.getContentChars().toString(), indent);
        case IndentedCodeBlock code -> renderCode(code.getContentChars().toString(), indent);
        case BlockQuote bq -> renderChildren(bq, indent + 360, true);
        case ThematicBreak hr -> renderRule();
        case TableBlock table -> renderTable(table);
        default -> {
          if (node.hasChildren()) {
            renderChildren(node, indent, quote);
          }
        }
      }
    }

    // ---- paragraphs -------------------------------------------------------

    private XWPFParagraph newBody(int indent, boolean quote) {
      XWPFParagraph p = doc.createParagraph();
      p.setAlignment(ParagraphAlignment.BOTH); // Justify alignment
      p.setSpacingAfter(120);
      p.setSpacingBetween(opt.lineSpacing());
      if (indent > 0) {
        p.setIndentationLeft(indent);
      }
      if (quote) {
        var bdr = ppr(p).addNewPBdr();
        var left = bdr.addNewLeft();
        left.setVal(STBorder.SINGLE);
        left.setSz(BigInteger.valueOf(18));
        left.setSpace(BigInteger.valueOf(8));
        left.setColor("A6A6A6");
      }
      return p;
    }

    private static CTPPr ppr(XWPFParagraph p) {
      return p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();
    }

    // ---- lists ------------------------------------------------------------

    private void renderList(ListBlock list, int indent, boolean quote) {
      boolean ordered = list instanceof OrderedList;
      int counter = ordered ? ((OrderedList) list).getStartNumber() : 0;

      for (Node item = list.getFirstChild(); item != null; item = item.getNext()) {
        if (!(item instanceof ListItem)) {
          continue;
        }
        String marker = ordered ? (counter++) + "." : "\u2022";
        boolean first = true;

        for (Node child = item.getFirstChild(); child != null; child = child.getNext()) {
          if (child instanceof Paragraph para) {
            XWPFParagraph p = newBody(indent + 720, quote);
            p.setSpacingAfter(60);
            if (first) {
              p.setIndentationHanging(360);
              XWPFRun m = p.createRun();
              styleBody(m);
              m.setText(marker);
              m.addTab();
              first = false;
            }
            writeInline(para, p, new Ctx(false, false, true));
          } else {
            renderBlock(child, indent + 720, quote);
          }
        }
      }
    }

    // ---- code / rule ------------------------------------------------------

    private void renderCode(String code, int indent) {
      String text = code.stripTrailing();
      for (String line : text.split("\\r?\\n", -1)) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(0);
        p.setSpacingBetween(1.0);
        if (indent > 0) {
          p.setIndentationLeft(indent);
        }
        CTShd shd = ppr(p).addNewShd();
        shd.setVal(STShd.CLEAR);
        shd.setFill("F2F2F2");

        XWPFRun r = p.createRun();
        r.setFontFamily(opt.codeFont());
        r.setFontSize(Math.max(8, opt.bodySizePt() - 1));
        r.setText(line.isEmpty() ? " " : line);
      }
      XWPFParagraph spacer = doc.createParagraph();
      spacer.setSpacingAfter(60);
    }

    private void renderRule() {
      XWPFParagraph p = doc.createParagraph();
      var bottom = ppr(p).addNewPBdr().addNewBottom();
      bottom.setVal(STBorder.SINGLE);
      bottom.setSz(BigInteger.valueOf(6));
      bottom.setSpace(BigInteger.valueOf(1));
      bottom.setColor("A6A6A6");
    }

    // ---- tables -----------------------------------------------------------

    private void renderTable(TableBlock tableBlock) {
      List<List<TableCell>> rows = new ArrayList<>();
      for (Node section = tableBlock.getFirstChild();
          section != null;
          section = section.getNext()) {
        for (Node row = section.getFirstChild(); row != null; row = row.getNext()) {
          if (row instanceof com.vladsch.flexmark.ext.tables.TableRow) {
            List<TableCell> cells = new ArrayList<>();
            for (Node c = row.getFirstChild(); c != null; c = c.getNext()) {
              if (c instanceof TableCell tc) {
                cells.add(tc);
              }
            }
            rows.add(cells);
          }
        }
      }
      if (rows.isEmpty()) {
        return;
      }

      int cols = rows.stream().mapToInt(List::size).max().orElse(1);
      XWPFTable table = doc.createTable(rows.size(), cols);
      table.setWidth("100%");
      table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");
      table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");
      table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");
      table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");
      table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");
      table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "BFBFBF");

      for (int r = 0; r < rows.size(); r++) {
        List<TableCell> mdCells = rows.get(r);
        for (int c = 0; c < cols; c++) {
          XWPFTableCell cell = table.getRow(r).getCell(c);
          if (c >= mdCells.size()) {
            continue;
          }
          TableCell md = mdCells.get(c);
          XWPFParagraph p = cell.getParagraphs().get(0);
          p.setSpacingAfter(0);
          p.setSpacingBetween(opt.lineSpacing());

          TableCell.Alignment alignment = md.getAlignment();
          ParagraphAlignment cellAlign = ParagraphAlignment.LEFT;
          if (alignment != null) {
            cellAlign =
                switch (alignment) {
                  case CENTER -> ParagraphAlignment.CENTER;
                  case RIGHT -> ParagraphAlignment.RIGHT;
                  default -> ParagraphAlignment.LEFT;
                };
          }
          p.setAlignment(cellAlign);

          boolean header = md.isHeader();
          if (header) {
            cell.setColor("D9E2F3");
          }
          writeInline(md, p, new Ctx(header, false, true));
        }
      }
      doc.createParagraph().setSpacingAfter(60);
    }

    // ---- inline -----------------------------------------------------------

    private void writeInline(Node parent, XWPFParagraph p, Ctx ctx) {
      for (Node n = parent.getFirstChild(); n != null; n = n.getNext()) {
        switch (n) {
          case Text t -> addRun(p, t.getChars().toString(), ctx);
          case StrongEmphasis s -> writeInline(s, p, ctx.withBold());
          case Emphasis e -> writeInline(e, p, ctx.withItalic());
          case Code c -> {
            XWPFRun r = p.createRun();
            r.setText(c.getText().toString());
            r.setFontFamily(opt.codeFont());
            r.setFontSize(Math.max(8, opt.bodySizePt() - 1));
            r.setBold(ctx.bold());
            r.setItalic(ctx.italic());
            var rpr = r.getCTR().isSetRPr() ? r.getCTR().getRPr() : r.getCTR().addNewRPr();
            CTShd shd = rpr.addNewShd();
            shd.setVal(STShd.CLEAR);
            shd.setFill("EDEDED");
          }
          case Link l -> {
            XWPFHyperlinkRun hr = p.createHyperlinkRun(l.getUrl().toString());
            hr.setText(l.getText().toString());
            hr.setColor("0563C1");
            hr.setUnderline(UnderlinePatterns.SINGLE);
            hr.setBold(ctx.bold());
            hr.setItalic(ctx.italic());
            if (ctx.bodyFont()) {
              styleBody(hr);
            }
          }
          case SoftLineBreak s -> addRun(p, " ", ctx);
          case HardLineBreak h -> p.createRun().addBreak();
          default -> {
            if (n.hasChildren()) {
              writeInline(n, p, ctx);
            } else {
              addRun(p, n.getChars().toString(), ctx);
            }
          }
        }
      }
    }

    private void addRun(XWPFParagraph p, String text, Ctx ctx) {
      XWPFRun r = p.createRun();
      r.setText(text);
      if (ctx.bold()) {
        r.setBold(true);
      }
      if (ctx.italic()) {
        r.setItalic(true);
      }
      if (ctx.bodyFont()) {
        styleBody(r);
      }
    }

    private void styleBody(XWPFRun r) {
      r.setFontFamily(opt.bodyFont());
      r.setFontSize(opt.bodySizePt());
    }
  }
}
