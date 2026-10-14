package tungnn.tutor.java.infrastructure.cron.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cronutils.model.CronType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CronUtilsTest {

  private static final String BASE_QUARTZ_CRON = "0 0 12 * * ? *";
  private static final int TARGET_MONTH = 10;
  private static final int TARGET_YEAR = 2026;

  @Nested
  @DisplayName("Tests for generateCronForDateRanges")
  class GenerateCronForDateRangesTests {

    @Nested
    @DisplayName("Happy Path Cases")
    class SuccessCases {

      @Test
      @DisplayName("Should generate valid cron when dateFrom equals dateEnd")
      void shouldGenerateCron_WhenSingleDayGiven() {
        // Given
        LocalDate dateFrom = LocalDate.of(2026, 10, 15);
        LocalDate dateEnd = LocalDate.of(2026, 10, 15);

        // When
        String result =
            CronUtils.generateCronForDateRanges(
                BASE_QUARTZ_CRON, CronType.QUARTZ, dateFrom, dateEnd);

        // Then
        assertEquals("0 0 12 15 10 ? 2026", result);
      }

      @Test
      @DisplayName("Should generate valid range cron when dateFrom is before dateEnd")
      void shouldGenerateCron_WhenDateRangeGiven() {
        // Given
        LocalDate dateFrom = LocalDate.of(2026, 10, 1);
        LocalDate dateEnd = LocalDate.of(2026, 10, 5);

        // When
        String result =
            CronUtils.generateCronForDateRanges(
                BASE_QUARTZ_CRON, CronType.QUARTZ, dateFrom, dateEnd);

        // Then
        assertEquals("0 0 12 1-5 10 ? 2026", result);
      }
    }

    @Nested
    @DisplayName("Validation Failure Cases")
    class ExceptionCases {

      @Test
      @DisplayName("Should throw exception when dateFrom or dateEnd is null")
      void shouldThrowException_WhenDatesAreNull() {
        LocalDate validDate = LocalDate.of(2026, 10, 1);

        IllegalArgumentException nullFromException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDateRanges(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, null, validDate));
        assertEquals("dateFrom and dateEnd must not be null", nullFromException.getMessage());

        IllegalArgumentException nullEndException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDateRanges(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, validDate, null));
        assertEquals("dateFrom and dateEnd must not be null", nullEndException.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when dateFrom is after dateEnd")
      void shouldThrowException_WhenDateFromIsAfterDateEnd() {
        LocalDate dateFrom = LocalDate.of(2026, 10, 10);
        LocalDate dateEnd = LocalDate.of(2026, 10, 5);

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDateRanges(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, dateFrom, dateEnd));

        assertEquals("dateFrom must be before or equal to dateEnd", exception.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when dateFrom and dateEnd are in different months")
      void shouldThrowException_WhenDatesAreInDifferentMonths() {
        LocalDate dateFrom = LocalDate.of(2026, 10, 25);
        LocalDate dateEnd = LocalDate.of(2026, 11, 5);

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDateRanges(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, dateFrom, dateEnd));

        assertEquals("dateFrom and dateEnd must be in the same month", exception.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when dateFrom and dateEnd are in different years")
      void shouldThrowException_WhenDatesAreInDifferentYears() {
        LocalDate dateFrom = LocalDate.of(2025, 10, 1);
        LocalDate dateEnd = LocalDate.of(2026, 10, 5);

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDateRanges(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, dateFrom, dateEnd));

        assertEquals("dateFrom and dateEnd must be in the same year", exception.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when base cron expression is invalid")
      void shouldThrowException_WhenBaseCronIsInvalid() {
        String invalidBaseCron = "INVALID CRON STRING";
        LocalDate dateFrom = LocalDate.of(2026, 10, 1);
        LocalDate dateEnd = LocalDate.of(2026, 10, 5);

        assertThrows(
            IllegalArgumentException.class,
            () ->
                CronUtils.generateCronForDateRanges(
                    invalidBaseCron, CronType.QUARTZ, dateFrom, dateEnd));
      }
    }
  }

  @Nested
  @DisplayName("Tests for generateCronForDayOfMonths")
  class GenerateCronForDayOfMonthsTests {

    @Nested
    @DisplayName("Happy Path Cases")
    class SuccessCases {

      @Test
      @DisplayName("Should generate valid cron for a single day of month")
      void shouldGenerateCron_WhenSingleDayGiven() {
        // Given
        Set<Integer> dayOfMonths = Set.of(15);

        // When
        String result =
            CronUtils.generateCronForDayOfMonths(
                BASE_QUARTZ_CRON, CronType.QUARTZ, dayOfMonths, TARGET_MONTH, TARGET_YEAR);

        // Then
        assertEquals("0 0 12 15 10 ? 2026", result);
      }

      @Test
      @DisplayName("Should generate valid sorted cron for multiple discrete days of month")
      void shouldGenerateCron_WhenMultipleDaysGiven() {
        // Given
        Set<Integer> dayOfMonths = Set.of(20, 1, 10, 5);

        // When
        String result =
            CronUtils.generateCronForDayOfMonths(
                BASE_QUARTZ_CRON, CronType.QUARTZ, dayOfMonths, TARGET_MONTH, TARGET_YEAR);

        // Then
        assertEquals("0 0 12 1,5,10,20 10 ? 2026", result);
      }
    }

    @Nested
    @DisplayName("Validation Failure Cases")
    class ExceptionCases {

      @Test
      @DisplayName("Should throw exception when dayOfMonths is null or empty")
      void shouldThrowException_WhenDayOfMonthsIsNullOrEmpty() {
        IllegalArgumentException nullException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, null, TARGET_MONTH, TARGET_YEAR));
        assertEquals("dayOfMonths cannot be null or empty", nullException.getMessage());

        IllegalArgumentException emptyException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON,
                        CronType.QUARTZ,
                        Collections.emptySet(),
                        TARGET_MONTH,
                        TARGET_YEAR));
        assertEquals("dayOfMonths cannot be null or empty", emptyException.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when dayOfMonths contains values outside 1-31")
      void shouldThrowException_WhenDayOfMonthIsOutOfRange() {
        Set<Integer> invalidLowDays = Set.of(0, 5);
        IllegalArgumentException lowException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON,
                        CronType.QUARTZ,
                        invalidLowDays,
                        TARGET_MONTH,
                        TARGET_YEAR));
        assertEquals("dayOfMonths must contain values between 1 and 31", lowException.getMessage());

        Set<Integer> invalidHighDays = Set.of(1, 32);
        IllegalArgumentException highException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON,
                        CronType.QUARTZ,
                        invalidHighDays,
                        TARGET_MONTH,
                        TARGET_YEAR));
        assertEquals(
            "dayOfMonths must contain values between 1 and 31", highException.getMessage());
      }

      @Test
      @DisplayName("Should throw exception when month is outside 1-12")
      void shouldThrowException_WhenMonthIsOutOfRange() {
        Set<Integer> validDays = Set.of(1, 5);

        IllegalArgumentException lowMonthException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, validDays, 0, TARGET_YEAR));
        assertEquals("month must be between 1 and 12", lowMonthException.getMessage());

        IllegalArgumentException highMonthException =
            assertThrows(
                IllegalArgumentException.class,
                () ->
                    CronUtils.generateCronForDayOfMonths(
                        BASE_QUARTZ_CRON, CronType.QUARTZ, validDays, 13, TARGET_YEAR));
        assertEquals("month must be between 1 and 12", highMonthException.getMessage());
      }
    }
  }
}
