package tungnn.tutor.java.core.lib.time;

import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Reference samples for the java.time API (JSR-310).
 *
 * <p>Covered areas: 1. Parsing and Formatting 2. Arithmetic 3. Adjustment 4. Comparison 5. Legacy
 * Conversion 6. Partial / special types (Year, YearMonth, MonthDay, DayOfWeek, Month)
 *
 * <p>Design notes: - All java.time types are immutable and thread-safe; every "mutating" call
 * returns a new instance. - DateTimeFormatter instances are immutable and safe to hold as
 * constants, unlike the legacy SimpleDateFormat.
 */
public final class DateTimeApiSamples {

  private static final ZoneId ZONE_TOKYO = ZoneId.of("Asia/Tokyo");
  private static final ZoneId ZONE_HANOI = ZoneId.of("Asia/Ho_Chi_Minh");

  private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private static final DateTimeFormatter PATTERN_DMY = DateTimeFormatter.ofPattern("dd/MM/uuuu");
  private static final DateTimeFormatter PATTERN_FULL =
      DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
  private static final DateTimeFormatter PATTERN_LOCALIZED =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(Locale.US);

  private DateTimeApiSamples() {}

  public static void main(String[] args) {
    parsingAndFormatting();
    arithmetic();
    adjustment();
    comparison();
    legacyConversion();
    specialTypes();
  }

  // ------------------------------------------------------------------
  // 1. Parsing and Formatting
  // ------------------------------------------------------------------
  private static void parsingAndFormatting() {
    section("1. Parsing and Formatting");

    // ISO-8601 parsing: every java.time type has a static parse(CharSequence).
    LocalDate isoDate = LocalDate.parse("2026-09-14");
    LocalDateTime isoDateTime = LocalDateTime.parse("2026-09-14T10:15:30");
    OffsetDateTime offsetDateTime = OffsetDateTime.parse("2026-09-14T10:15:30+07:00");
    ZonedDateTime zonedDateTime = ZonedDateTime.parse("2026-09-14T10:15:30+09:00[Asia/Tokyo]");
    Instant instant = Instant.parse("2026-09-14T03:15:30Z");

    print("LocalDate.parse", isoDate);
    print("LocalDateTime.parse", isoDateTime);
    print("OffsetDateTime.parse", offsetDateTime);
    print("ZonedDateTime.parse", zonedDateTime);
    print("Instant.parse", instant);

    // Custom pattern parsing. Prefer 'uuuu' (proleptic year) over 'yyyy'
    // (era year) so that STRICT resolution can reject invalid input.
    LocalDate custom = LocalDate.parse("14/09/2026", PATTERN_DMY);
    print("parse dd/MM/uuuu", custom);

    // Formatting is the inverse operation.
    print("format dd/MM/uuuu", isoDateTime.format(PATTERN_DMY));
    print("format uuuu-MM-dd HH:mm:ss.SSS", isoDateTime.format(PATTERN_FULL));
    print("format localized MEDIUM", isoDateTime.format(PATTERN_LOCALIZED));
    print("format ISO_LOCAL_DATE_TIME", isoDateTime.format(ISO_LOCAL));

    // Instant has no zone/offset, so it cannot be formatted by a
    // local-based formatter directly; attach a zone first.
    print("Instant formatted via zone", PATTERN_FULL.withZone(ZONE_HANOI).format(instant));

    // Parsing failures throw DateTimeParseException (unchecked).
    try {
      LocalDate.parse("2026-02-30");
    } catch (DateTimeParseException e) {
      print("invalid date rejected", e.getMessage());
    }

    // Parse directly into a specific type using a query method reference.
    LocalDate viaQuery = PATTERN_DMY.parse("01/01/2027", LocalDate::from);
    print("parse via query", viaQuery);
  }

  // ------------------------------------------------------------------
  // 2. Arithmetic
  // ------------------------------------------------------------------
  private static void arithmetic() {
    section("2. Arithmetic");

    LocalDate date = LocalDate.of(2026, Month.JANUARY, 31);
    LocalDateTime dateTime = LocalDateTime.of(2026, 9, 14, 10, 15, 30);

    // plus/minus family - each returns a new instance.
    print("base date", date);
    print("plusDays(15)", date.plusDays(15));
    print("plusMonths(1) [clamped to end of Feb]", date.plusMonths(1));
    print("minusWeeks(2)", date.minusWeeks(2));
    print("plusYears(1)", date.plusYears(1));

    // Generic arithmetic via ChronoUnit.
    print("plus(3, ChronoUnit.DECADES)", date.plus(3, ChronoUnit.DECADES));
    print("dateTime.plus(90, MINUTES)", dateTime.plus(90, ChronoUnit.MINUTES));

    // Period = date-based amount (years, months, days).
    Period period = Period.of(1, 2, 3);
    print("plus(Period P1Y2M3D)", date.plus(period));

    // Duration = time-based amount (seconds, nanos).
    Duration duration = Duration.ofHours(36).plusMinutes(45);
    print("plus(Duration PT36H45M)", dateTime.plus(duration));
    print("duration.toDays / toHoursPart", duration.toDays() + "d " + duration.toHoursPart() + "h");

    // Measuring elapsed amounts.
    LocalDate start = LocalDate.of(2026, 1, 1);
    LocalDate end = LocalDate.of(2027, 3, 20);
    Period between = Period.between(start, end);
    print(
        "Period.between",
        between
            + " -> "
            + between.getYears()
            + "y "
            + between.getMonths()
            + "m "
            + between.getDays()
            + "d");
    print("ChronoUnit.DAYS.between", ChronoUnit.DAYS.between(start, end));
    print("ChronoUnit.MONTHS.between", ChronoUnit.MONTHS.between(start, end));

    Instant t0 = Instant.parse("2026-09-14T00:00:00Z");
    Instant t1 = Instant.parse("2026-09-15T06:30:00Z");
    print("Duration.between(instants)", Duration.between(t0, t1));

    // Daylight-saving-aware arithmetic: plusDays on ZonedDateTime keeps the
    // local wall-clock time, plus(Duration) adds exact elapsed time.
    ZonedDateTime zdt = ZonedDateTime.of(2026, 3, 28, 23, 0, 0, 0, ZoneId.of("Europe/Paris"));
    print("ZonedDateTime base", zdt);
    print("plusDays(1) [wall clock]", zdt.plusDays(1));
    print("plus(24h) [exact elapsed]", zdt.plus(Duration.ofHours(24)));
  }

  // ------------------------------------------------------------------
  // 3. Adjustment
  // ------------------------------------------------------------------
  private static void adjustment() {
    section("3. Adjustment");

    LocalDate date = LocalDate.of(2026, 9, 14);
    LocalDateTime dateTime = LocalDateTime.of(2026, 9, 14, 10, 15, 30, 123_000_000);

    // withXxx - replace a single field.
    print("withDayOfMonth(1)", date.withDayOfMonth(1));
    print("withMonth(12)", date.withMonth(12));
    print("with(ChronoField.DAY_OF_YEAR, 200)", date.with(ChronoField.DAY_OF_YEAR, 200));

    // Built-in TemporalAdjusters.
    print("firstDayOfMonth", date.with(TemporalAdjusters.firstDayOfMonth()));
    print("lastDayOfMonth", date.with(TemporalAdjusters.lastDayOfMonth()));
    print("firstDayOfNextMonth", date.with(TemporalAdjusters.firstDayOfNextMonth()));
    print("lastDayOfYear", date.with(TemporalAdjusters.lastDayOfYear()));
    print("next(FRIDAY)", date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
    print("nextOrSame(MONDAY)", date.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
    print("previous(SUNDAY)", date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)));
    print(
        "firstInMonth(WEDNESDAY)", date.with(TemporalAdjusters.firstInMonth(DayOfWeek.WEDNESDAY)));
    print(
        "dayOfWeekInMonth(3, TUESDAY)",
        date.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.TUESDAY)));
    print("lastInMonth(FRIDAY)", date.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY)));

    // Truncation - zero out everything below the given unit.
    print("truncatedTo(HOURS)", dateTime.truncatedTo(ChronoUnit.HOURS));
    print("truncatedTo(SECONDS)", dateTime.truncatedTo(ChronoUnit.SECONDS));

    // Custom adjuster: next working day.
    TemporalAdjuster nextWorkingDay =
        TemporalAdjusters.ofDateAdjuster(
            d -> {
              DayOfWeek dow = d.getDayOfWeek();
              int shift =
                  switch (dow) {
                    case FRIDAY -> 3;
                    case SATURDAY -> 2;
                    default -> 1;
                  };
              return d.plusDays(shift);
            });
    print("custom nextWorkingDay (from Mon)", date.with(nextWorkingDay));
    print("custom nextWorkingDay (from Fri)", LocalDate.of(2026, 9, 18).with(nextWorkingDay));

    // Composition between date and time types.
    print("LocalDate.atTime", date.atTime(LocalTime.NOON));
    print("LocalDate.atStartOfDay(zone)", date.atStartOfDay(ZONE_HANOI));
    print("LocalDateTime.toLocalDate", dateTime.toLocalDate());

    // Zone conversion: same instant, different wall clock.
    ZonedDateTime hanoi = dateTime.atZone(ZONE_HANOI);
    print("Hanoi", hanoi);
    print("withZoneSameInstant(Tokyo)", hanoi.withZoneSameInstant(ZONE_TOKYO));
    print("withZoneSameLocal(Tokyo)", hanoi.withZoneSameLocal(ZONE_TOKYO));
  }

  // ------------------------------------------------------------------
  // 4. Comparison
  // ------------------------------------------------------------------
  private static void comparison() {
    section("4. Comparison");

    LocalDate a = LocalDate.of(2026, 9, 14);
    LocalDate b = LocalDate.of(2026, 12, 25);

    print("a.isBefore(b)", a.isBefore(b));
    print("a.isAfter(b)", a.isAfter(b));
    print("a.isEqual(b)", a.isEqual(b));
    print("a.compareTo(b)", a.compareTo(b));
    print("a.equals(a copy)", a.equals(LocalDate.of(2026, 9, 14)));

    LocalTime t1 = LocalTime.of(9, 30);
    LocalTime t2 = LocalTime.of(17, 0);
    print("t1.isBefore(t2)", t1.isBefore(t2));

    // equals() vs isEqual()/compareTo() on ZonedDateTime:
    // equals() compares zone + local time + offset; isEqual() compares the instant.
    ZonedDateTime hanoi = ZonedDateTime.of(2026, 9, 14, 10, 0, 0, 0, ZONE_HANOI);
    ZonedDateTime tokyo = hanoi.withZoneSameInstant(ZONE_TOKYO);
    print("hanoi", hanoi);
    print("tokyo (same instant)", tokyo);
    print("hanoi.equals(tokyo)", hanoi.equals(tokyo));
    print("hanoi.isEqual(tokyo)", hanoi.isEqual(tokyo));
    print("hanoi.toInstant().equals(...)", hanoi.toInstant().equals(tokyo.toInstant()));

    // Instant comparison.
    Instant i1 = Instant.parse("2026-09-14T00:00:00Z");
    Instant i2 = Instant.parse("2026-09-14T01:00:00Z");
    print("i1.isBefore(i2)", i1.isBefore(i2));

    // Cross-chronology safe comparison.
    print("ChronoLocalDate.timeLineOrder", ChronoLocalDate.timeLineOrder().compare(a, b));

    // Range check helper.
    print("isWithin(2026-10-01, a..b)", isWithin(LocalDate.of(2026, 10, 1), a, b));

    // Duration / Period comparison. Period is NOT Comparable because
    // month length is ambiguous; Duration is Comparable.
    print("Duration compare", Duration.ofMinutes(90).compareTo(Duration.ofHours(1)));
    print("Period equals (1M vs 30D)", Period.ofMonths(1).equals(Period.ofDays(30)));
  }

  /** Inclusive range check. */
  private static boolean isWithin(LocalDate value, LocalDate from, LocalDate to) {
    return !value.isBefore(from) && !value.isAfter(to);
  }

  // ------------------------------------------------------------------
  // 5. Legacy Conversion
  // ------------------------------------------------------------------
  private static void legacyConversion() {
    section("5. Legacy Conversion");

    // java.util.Date <-> Instant (Date is an instant on the timeline).
    Date legacyDate = new Date();
    Instant fromDate = legacyDate.toInstant();
    Date backToDate = Date.from(fromDate);
    print("java.util.Date -> Instant", fromDate);
    print("Instant -> java.util.Date", backToDate);

    // java.util.Date -> LocalDateTime requires an explicit zone.
    LocalDateTime ldt = LocalDateTime.ofInstant(fromDate, ZONE_HANOI);
    print("Date -> LocalDateTime (Hanoi)", ldt);
    print("LocalDateTime -> Date", Date.from(ldt.atZone(ZONE_HANOI).toInstant()));

    // java.util.Calendar / GregorianCalendar.
    Calendar calendar = new GregorianCalendar(2026, Calendar.SEPTEMBER, 14, 10, 15, 30);
    calendar.setTimeZone(TimeZone.getTimeZone(ZONE_HANOI));
    print("Calendar -> Instant", calendar.toInstant());
    GregorianCalendar gregorian = (GregorianCalendar) calendar;
    ZonedDateTime fromCalendar = gregorian.toZonedDateTime();
    print("GregorianCalendar -> ZonedDateTime", fromCalendar);
    print("ZonedDateTime -> GregorianCalendar", GregorianCalendar.from(fromCalendar).getTime());

    // java.util.TimeZone <-> ZoneId.
    TimeZone legacyZone = TimeZone.getTimeZone("Asia/Tokyo");
    print("TimeZone -> ZoneId", legacyZone.toZoneId());
    print("ZoneId -> TimeZone", TimeZone.getTimeZone(ZONE_HANOI).getID());

    // java.sql types (bridging methods, no zone conversion involved).
    java.sql.Date sqlDate = java.sql.Date.valueOf(LocalDate.of(2026, 9, 14));
    print("java.sql.Date -> LocalDate", sqlDate.toLocalDate());
    java.sql.Time sqlTime = java.sql.Time.valueOf(LocalTime.of(10, 15, 30));
    print("java.sql.Time -> LocalTime", sqlTime.toLocalTime());
    java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(ldt);
    print("java.sql.Timestamp -> LocalDateTime", sqlTimestamp.toLocalDateTime());
    print("Timestamp.from(Instant)", java.sql.Timestamp.from(fromDate));

    // Epoch millis bridging.
    long epochMilli = System.currentTimeMillis();
    print("epochMilli -> Instant", Instant.ofEpochMilli(epochMilli));
    print("Instant -> epochMilli", fromDate.toEpochMilli());
    print("LocalDate -> epochDay", LocalDate.of(2026, 9, 14).toEpochDay());
    print("epochDay -> LocalDate", LocalDate.ofEpochDay(20710));

    // Legacy SimpleDateFormat pattern -> DateTimeFormatter equivalent.
    print(
        "DateTimeFormatter from legacy pattern",
        DateTimeFormatter.ofPattern("EEE, dd MMM uuuu HH:mm:ss", Locale.ENGLISH).format(ldt));
  }

  // ------------------------------------------------------------------
  // 6. Special / partial types
  // ------------------------------------------------------------------
  private static void specialTypes() {
    section("6. Special Types: Year, YearMonth, MonthDay, Month, DayOfWeek");

    // Year - a single calendar year.
    Year year = Year.of(2026);
    print("Year", year);
    print("Year.isLeap", year.isLeap());
    print("Year.length", year.length());
    print("Year.plusYears(2)", year.plusYears(2));
    print("Year.atDay(100)", year.atDay(100));
    print("Year.atMonth(FEBRUARY)", year.atMonth(Month.FEBRUARY));
    print("Year.isValidMonthDay(Feb 29)", year.isValidMonthDay(MonthDay.of(2, 29)));
    print("Year.parse", Year.parse("2030"));

    // YearMonth - a month within a year; ideal for billing periods, card expiry.
    YearMonth yearMonth = YearMonth.of(2026, Month.FEBRUARY);
    print("YearMonth", yearMonth);
    print("YearMonth.lengthOfMonth", yearMonth.lengthOfMonth());
    print("YearMonth.atDay(15)", yearMonth.atDay(15));
    print("YearMonth.atEndOfMonth", yearMonth.atEndOfMonth());
    print("YearMonth.plusMonths(13)", yearMonth.plusMonths(13));
    print("YearMonth.format(MM/uuuu)", yearMonth.format(DateTimeFormatter.ofPattern("MM/uuuu")));
    print("YearMonth.parse", YearMonth.parse("2026-12"));
    print("MONTHS.between yearMonths", ChronoUnit.MONTHS.between(yearMonth, YearMonth.of(2027, 5)));

    // MonthDay - a recurring day in the year, e.g. birthdays, anniversaries.
    MonthDay monthDay = MonthDay.of(Month.FEBRUARY, 29);
    print("MonthDay", monthDay);
    print("MonthDay.isValidYear(2026)", monthDay.isValidYear(2026));
    print("MonthDay.atYear(2026) [clamped]", monthDay.atYear(2026));
    print("MonthDay.atYear(2028)", monthDay.atYear(2028));
    print("MonthDay.parse", MonthDay.parse("--12-25"));

    // Month / DayOfWeek enums.
    Month month = Month.SEPTEMBER;
    print("Month.getValue", month.getValue());
    print("Month.length(leap=false)", month.length(false));
    print("Month.plus(5)", month.plus(5));
    print("Month.firstMonthOfQuarter", month.firstMonthOfQuarter());
    print(
        "Month.getDisplayName",
        month.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH));

    DayOfWeek dayOfWeek = LocalDate.of(2026, 9, 14).getDayOfWeek();
    print("DayOfWeek", dayOfWeek);
    print("DayOfWeek.plus(3)", dayOfWeek.plus(3));
    print(
        "DayOfWeek.getDisplayName SHORT",
        dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH));

    // ZoneOffset / ZoneId / OffsetTime.
    print("ZoneOffset.ofHours(7)", ZoneOffset.ofHours(7));
    print("ZoneId rules offset now", ZONE_HANOI.getRules().getOffset(Instant.now()));
    print("OffsetTime", OffsetTime.of(LocalTime.of(10, 15), ZoneOffset.ofHours(7)));

    // Clock - injectable time source; essential for deterministic tests.
    Clock fixedClock = Clock.fixed(Instant.parse("2026-09-14T03:00:00Z"), ZONE_HANOI);
    print("LocalDateTime.now(fixedClock)", LocalDateTime.now(fixedClock));
    print("Clock.systemUTC millis", Clock.systemUTC().instant().getEpochSecond());
  }

  // ------------------------------------------------------------------
  // Output helpers
  // ------------------------------------------------------------------
  private static void section(String title) {
    System.out.println();
    System.out.println("==== " + title + " ====");
  }

  private static void print(String label, Object value) {
    System.out.printf("%-42s : %s%n", label, value);
  }
}
