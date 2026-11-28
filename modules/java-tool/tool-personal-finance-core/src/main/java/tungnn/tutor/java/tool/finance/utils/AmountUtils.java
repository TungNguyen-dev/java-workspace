package tungnn.tutor.java.tool.finance.utils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public final class AmountUtils {

    private static final Pattern NUMBER_TOKEN = Pattern.compile("\\d(?:[\\d.,]*\\d)?");
    private static final Pattern NON_NUMERIC = Pattern.compile("[^0-9.,-]");

    private AmountUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static Optional<BigDecimal> parse(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        var cleaned = NON_NUMERIC.matcher(raw).replaceAll("");
        var negative = cleaned.startsWith("-");
        cleaned = cleaned.replace("-", "");
        if (cleaned.isEmpty() || !cleaned.chars().anyMatch(Character::isDigit)) {
            return Optional.empty();
        }
        var normalized = normalizeSeparators(cleaned);
        try {
            var value = new BigDecimal(normalized);
            return Optional.of(negative ? value.negate() : value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Set<String> numbersIn(String text) {
        var result = new HashSet<String>();
        if (text == null) {
            return result;
        }
        var matcher = NUMBER_TOKEN.matcher(text);
        while (matcher.find()) {
            parse(matcher.group()).ifPresent(value -> result.add(canonical(value)));
        }
        return result;
    }

    public static String canonical(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private static String normalizeSeparators(String s) {
        var lastDot = s.lastIndexOf('.');
        var lastComma = s.lastIndexOf(',');
        if (lastDot >= 0 && lastComma >= 0) {
            var decimal = Math.max(lastDot, lastComma);
            var integerPart = s.substring(0, decimal).replace(".", "").replace(",", "");
            return integerPart + "." + s.substring(decimal + 1);
        }
        if (lastDot >= 0) {
            return singleSeparator(s, '.');
        }
        if (lastComma >= 0) {
            return singleSeparator(s, ',');
        }
        return s;
    }

    private static String singleSeparator(String s, char separator) {
        var count = s.chars().filter(c -> c == separator).count();
        var last = s.lastIndexOf(separator);
        var digitsAfter = s.length() - last - 1;
        // VND has no minor unit, so one separator followed by exactly three digits is a thousands separator.
        var thousands = count > 1 || digitsAfter == 3;
        return thousands ? s.replace(String.valueOf(separator), "") : s.replace(separator, '.');
    }
}
