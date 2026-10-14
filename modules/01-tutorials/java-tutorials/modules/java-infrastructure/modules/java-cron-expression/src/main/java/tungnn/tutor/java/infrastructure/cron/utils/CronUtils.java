package tungnn.tutor.java.infrastructure.cron.utils;

import static com.cronutils.model.field.expression.FieldExpressionFactory.and;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;

import com.cronutils.builder.CronBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.mapper.CronMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.FieldExpression;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class CronUtils {

  private CronUtils() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  // 1. PARSE & VALIDATE

  // Parse and validate a cron expression string for a specific CronType
  public static Cron parseAndValidate(String cronExpression, CronType cronType) {
    Objects.requireNonNull(cronExpression, "cronExpression must not be null");
    Objects.requireNonNull(cronType, "cronType must not be null");
    CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType));
    Cron cron = parser.parse(cronExpression);
    cron.validate();
    return cron;
  }

  // Check whether a cron expression is valid
  public static boolean isValid(String cronExpression, CronType cronType) {
    try {
      parseAndValidate(cronExpression, cronType);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  // 2. DESCRIBE

  // Describe a parsed Cron object in human-readable text for a given locale
  public static String describe(Cron cron, Locale locale) {
    Objects.requireNonNull(cron, "cron must not be null");
    Objects.requireNonNull(locale, "locale must not be null");
    return CronDescriptor.instance(locale).describe(cron);
  }

  // Describe a cron expression string for a given locale
  public static String describe(String cronExpression, CronType cronType, Locale locale) {
    Cron cron = parseAndValidate(cronExpression, cronType);
    return describe(cron, locale);
  }

  // Describe a cron expression string using default system locale
  public static String describe(String cronExpression, CronType cronType) {
    return describe(cronExpression, cronType, Locale.getDefault());
  }

  // 3. MIGRATE

  // Convert a cron expression from one format dialect to another
  public static String migrate(String cronExpression, CronType sourceType, CronMapper mapper) {
    Objects.requireNonNull(mapper, "mapper must not be null");
    Cron sourceCron = parseAndValidate(cronExpression, sourceType);
    return mapper.map(sourceCron).asString();
  }

  // 4. EXECUTION TIME CALCULATOR

  // Calculate the next execution time from a reference timestamp
  public static Optional<ZonedDateTime> getNextExecution(
      String cronExpression, CronType cronType, ZonedDateTime fromTime) {
    Cron cron = parseAndValidate(cronExpression, cronType);
    return ExecutionTime.forCron(cron).nextExecution(fromTime);
  }

  // Calculate the previous execution time from a reference timestamp
  public static Optional<ZonedDateTime> getLastExecution(
      String cronExpression, CronType cronType, ZonedDateTime fromTime) {
    Cron cron = parseAndValidate(cronExpression, cronType);
    return ExecutionTime.forCron(cron).lastExecution(fromTime);
  }

  // Calculate remaining duration until the next execution
  public static Optional<Duration> getTimeToNextExecution(
      String cronExpression, CronType cronType, ZonedDateTime fromTime) {
    Cron cron = parseAndValidate(cronExpression, cronType);
    return ExecutionTime.forCron(cron).timeToNextExecution(fromTime);
  }

  // Calculate elapsed duration since the last execution
  public static Optional<Duration> getTimeFromLastExecution(
      String cronExpression, CronType cronType, ZonedDateTime fromTime) {
    Cron cron = parseAndValidate(cronExpression, cronType);
    return ExecutionTime.forCron(cron).timeFromLastExecution(fromTime);
  }

  // 5. COMPLEX CRON GENERATION

  // Generate a new cron expression for a date range between dateFrom and dateEnd
  public static String generateCronForDateRanges(
      String cronExpression, CronType cronType, LocalDate dateFrom, LocalDate dateEnd) {

    validateDateRange(dateFrom, dateEnd);

    Cron baseCron = parseAndValidate(cronExpression, cronType);

    int startDay = dateFrom.getDayOfMonth();
    int endDay = dateEnd.getDayOfMonth();
    int month = dateFrom.getMonthValue();
    int year = dateFrom.getYear();

    FieldExpression domExpression = (startDay == endDay) ? on(startDay) : between(startDay, endDay);

    return buildCronExpression(baseCron, cronType, domExpression, month, year);
  }

  // Validate start date and end date range
  private static void validateDateRange(LocalDate dateFrom, LocalDate dateEnd) {
    if (dateFrom == null || dateEnd == null) {
      throw new IllegalArgumentException("dateFrom and dateEnd must not be null");
    }
    if (dateFrom.isAfter(dateEnd)) {
      throw new IllegalArgumentException("dateFrom must be before or equal to dateEnd");
    }
    if (dateFrom.getMonthValue() != dateEnd.getMonthValue()) {
      throw new IllegalArgumentException("dateFrom and dateEnd must be in the same month");
    }
    if (dateFrom.getYear() != dateEnd.getYear()) {
      throw new IllegalArgumentException("dateFrom and dateEnd must be in the same year");
    }
  }

  // Generate a new cron expression for discrete days of the month
  public static String generateCronForDayOfMonths(
      String cronExpression, CronType cronType, Set<Integer> dayOfMonths, int month, int year) {

    validateDayOfMonths(dayOfMonths);
    validateMonth(month);

    Cron baseCron = parseAndValidate(cronExpression, cronType);

    List<FieldExpression> domExpressions = new ArrayList<>();
    dayOfMonths.stream().sorted().forEach(day -> domExpressions.add(on(day)));

    FieldExpression finalDomExpression =
        domExpressions.size() == 1 ? domExpressions.getFirst() : and(domExpressions);

    return buildCronExpression(baseCron, cronType, finalDomExpression, month, year);
  }

  // Validate dayOfMonths set
  private static void validateDayOfMonths(Set<Integer> dayOfMonths) {
    if (dayOfMonths == null || dayOfMonths.isEmpty()) {
      throw new IllegalArgumentException("dayOfMonths cannot be null or empty");
    }
    for (Integer day : dayOfMonths) {
      if (day == null || day < 1 || day > 31) {
        throw new IllegalArgumentException("dayOfMonths must contain values between 1 and 31");
      }
    }
  }

  // Validate month range (1-12)
  private static void validateMonth(int month) {
    if (month < 1 || month > 12) {
      throw new IllegalArgumentException("month must be between 1 and 12");
    }
  }

  // Reconstruct new cron expression using CronBuilder
  private static String buildCronExpression(
      Cron baseCron, CronType cronType, FieldExpression domExpression, int month, int year) {

    CronBuilder builder = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(cronType));

    // Copy standard time fields from base cron
    copyFieldIfPresent(baseCron, builder, CronFieldName.SECOND);
    copyFieldIfPresent(baseCron, builder, CronFieldName.MINUTE);
    copyFieldIfPresent(baseCron, builder, CronFieldName.HOUR);

    // Set new date fields
    builder.withDoM(domExpression);
    builder.withMonth(on(month));

    // Update YEAR field if supported by cron dialect (e.g. QUARTZ)
    if (baseCron.retrieve(CronFieldName.YEAR) != null) {
      builder.withYear(on(year));
    }

    // Handle DAY_OF_WEEK compatibility for QUARTZ and SPRING formats
    if (baseCron.retrieve(CronFieldName.DAY_OF_WEEK) != null) {
      if (cronType == CronType.QUARTZ || cronType == CronType.SPRING) {
        builder.withDoW(questionMark());
      } else {
        builder.withDoW(baseCron.retrieve(CronFieldName.DAY_OF_WEEK).getExpression());
      }
    }

    return builder.instance().asString();
  }

  // Helper to copy time field expressions from source cron to target builder
  private static void copyFieldIfPresent(
      Cron source, CronBuilder builder, CronFieldName fieldName) {
    var field = source.retrieve(fieldName);
    if (field == null) {
      return;
    }
    switch (fieldName) {
      case SECOND -> builder.withSecond(field.getExpression());
      case MINUTE -> builder.withMinute(field.getExpression());
      case HOUR -> builder.withHour(field.getExpression());
      default -> throw new IllegalArgumentException("Unsupported time field: " + fieldName);
    }
  }
}
