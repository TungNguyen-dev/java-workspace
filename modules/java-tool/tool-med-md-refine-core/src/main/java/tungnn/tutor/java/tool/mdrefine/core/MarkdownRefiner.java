package tungnn.tutor.java.tool.mdrefine.core;

import static java.util.stream.Collectors.joining;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownRefiner {

  private static final String MARK = "\uE000";

  // Regions that must not be touched: inline code, URLs in links, bare URLs
  private static final Pattern PROTECTED = Pattern.compile("`[^`]*`|\\]\\([^)]*\\)|https?://\\S+");

  private static final Pattern INVISIBLE = Pattern.compile("[\\u200B\\u200C\\u200D\\uFEFF\\u00AD]");
  private static final Pattern BOOK_BRACKETS = Pattern.compile("[《》]");
  private static final Pattern BULLET = Pattern.compile("^(\\s*)[•●▪◦·+*]\\s+(.*)$");
  private static final Pattern HEADING_NO_SPACE = Pattern.compile("^(#{1,6})([^#\\s].*)$");
  private static final Pattern HEADING_TRAILING = Pattern.compile("\\s+#+\\s*$");
  private static final Pattern MULTI_SPACE = Pattern.compile("[ ]{2,}");
  private static final Pattern SPACE_BEFORE_PUNCT = Pattern.compile("\\s+([,.;:!?])(?=\\s|$)");
  private static final Pattern NO_SPACE_AFTER_PUNCT = Pattern.compile("([,;:!?])(?=\\p{L})");
  private static final Pattern BOLD_SPAN = Pattern.compile("\\*\\*([^*]+)\\*\\*");
  private static final Pattern DOSAGE = Pattern.compile("(?<=\\d)(mg|kg|g|ml)\\b");
  private static final Pattern LIST_OR_SPECIAL_START =
      Pattern.compile("^\\s*([-*+>#]|\\d+[.)])\\s|^\\s*\\|");

  private static final Pattern TABLE_ROW = Pattern.compile("^\\s*\\|");

  private final boolean spaceBeforeUnit;

  public MarkdownRefiner(boolean spaceBeforeUnit) {
    this.spaceBeforeUnit = spaceBeforeUnit;
  }

  public static MarkdownRefiner defaultMedical() {
    return new MarkdownRefiner(true); // "12g" -> "12 g"
  }

  private static String protect(String text, UnaryOperator<String> fn) {
    var holders = new ArrayList<String>();
    var m = PROTECTED.matcher(text);
    var sb = new StringBuilder();
    while (m.find()) {
      holders.add(m.group());
      m.appendReplacement(sb, Matcher.quoteReplacement(MARK + (holders.size() - 1) + MARK));
    }
    m.appendTail(sb);

    var result = fn.apply(sb.toString());
    for (int i = 0; i < holders.size(); i++) {
      result = result.replace(MARK + i + MARK, holders.get(i));
    }
    return result;
  }

  // ---------- rules ----------

  public String refine(String blockType, String markdown) {
    if (blockType.contains("Code")) {
      return markdown;
    }

    var text = normalizeChars(markdown);

    if (blockType.contains("Table")) {
      return text.lines().map(String::stripTrailing).collect(joining("\n"));
    }
    if (blockType.contains("Paragraph")) {
      text = unwrapHardLineBreaks(text);
    }
    return text.lines().map(this::refineLine).collect(joining("\n"));
  }

  private String normalizeChars(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFC); // normalize Vietnamese diacritics
    s = INVISIBLE.matcher(s).replaceAll("");
    s = BOOK_BRACKETS.matcher(s).replaceAll(""); // strip 《 》 around book titles
    return s.replace('\u00A0', ' ').replace("\t", "    ").replace("\r\n", "\n");
  }

  private String unwrapHardLineBreaks(String s) {
    var lines = s.split("\n", -1);
    var out = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      var line = lines[i];
      out.append(line);
      if (i == lines.length - 1) break;
      var next = lines[i + 1];
      boolean hardBreak =
          line.endsWith("  ") || line.isBlank() || next.isBlank() || TABLE_ROW.matcher(line).find();
      boolean nextIsSpecial = LIST_OR_SPECIAL_START.matcher(next).find();
      if (hardBreak || nextIsSpecial) {
        out.append('\n');
      } else if (line.endsWith("-") && !line.endsWith(" -") && !line.endsWith("--")) {
        out.setLength(out.length() - 1); // "thanh-\nnhiệt" -> "thanhnhiệt"
      } else {
        out.append(' ');
      }
    }
    return out.toString();
  }

  private String refineLine(String line) {
    if (line.isBlank()) return "";

    // Markdown table row (| a | b |, |---|---|): keep the structure, only trim extra whitespace at
    // both ends.
    if (TABLE_ROW.matcher(line).find()) {
      return line.strip();
    }

    line = BULLET.matcher(line).replaceFirst("$1- $2");

    var indent = line.substring(0, line.length() - line.stripLeading().length());
    var body = line.strip();

    if (body.startsWith("#")) {
      body = HEADING_NO_SPACE.matcher(body).replaceFirst("$1 $2");
      body = HEADING_TRAILING.matcher(body).replaceFirst("");
      indent = "";
    }
    return indent + protect(body, this::fixInline);
  }

  private String fixInline(String s) {
    s = MULTI_SPACE.matcher(s).replaceAll(" ");
    s = SPACE_BEFORE_PUNCT.matcher(s).replaceAll("$1");
    s = NO_SPACE_AFTER_PUNCT.matcher(s).replaceAll("$1 ");
    s =
        BOLD_SPAN
            .matcher(s)
            .replaceAll(r -> {
              var inner = r.group(1).strip();
              return Matcher.quoteReplacement(inner.isEmpty() ? r.group() : "**" + inner + "**");
            });
    if (spaceBeforeUnit) {
      s = DOSAGE.matcher(s).replaceAll(" $1");
    }
    return s;
  }
}
