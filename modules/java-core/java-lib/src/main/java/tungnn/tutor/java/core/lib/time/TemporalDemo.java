package tungnn.tutor.java.core.lib.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.*;

public class TemporalDemo {

  static void main(String[] args) {

    // 1. Temporal & TemporalAccessor
    // LocalDate implements Temporal (which extends TemporalAccessor)
    Temporal temporalObj = LocalDate.of(2026, 9, 10);
    // Read-only view
    TemporalAccessor accessorObj = temporalObj;

    System.out.println("--- 1. Read values (TemporalAccessor) ---");
    // Read-only access: read values through a TemporalField
    int year = accessorObj.get(ChronoField.YEAR);
    int dayOfWeek = accessorObj.get(ChronoField.DAY_OF_WEEK);
    System.out.println("Year: " + year + ", Day of Week (1-7): " + dayOfWeek);

    // 2. TemporalField
    System.out.println("\n--- 2. TemporalField ---");
    // Interrogate a Temporal object to check which fields it supports
    TemporalField dayOfMonthField = ChronoField.DAY_OF_MONTH;
    if (temporalObj.isSupported(dayOfMonthField)) {
      System.out.println("Day of month supported value: " + temporalObj.get(dayOfMonthField));
    }

    // 3. TemporalUnit & TemporalAmount
    System.out.println("\n--- 3. TemporalUnit & TemporalAmount ---");
    TemporalUnit daysUnit = ChronoUnit.DAYS;
    // Period implements TemporalAmount
    TemporalAmount periodTwoWeeks = Period.ofWeeks(2);

    // Add time using a TemporalUnit or a TemporalAmount
    Temporal futureDate1 = temporalObj.plus(10, daysUnit);
    Temporal futureDate2 = temporalObj.plus(periodTwoWeeks);
    System.out.println("+ 10 days (via TemporalUnit): " + futureDate1);
    System.out.println("+ 2 weeks (via TemporalAmount): " + futureDate2);

    // 4. TemporalAdjuster
    System.out.println("\n--- 4. TemporalAdjuster ---");
    // Use a built-in adjuster to move the date to the next Friday
    TemporalAdjuster nextFridayAdjuster = TemporalAdjusters.next(DayOfWeek.FRIDAY);
    Temporal nextFriday = temporalObj.with(nextFridayAdjuster);

    // Custom TemporalAdjuster: custom logic that moves the date to the end of the month
    TemporalAdjuster lastDayOfMonth =
        temporal ->
            temporal.with(
                ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
    Temporal endOfMonth = temporalObj.with(lastDayOfMonth);

    System.out.println("Next Friday: " + nextFriday);
    System.out.println("Last day of month (Custom Adjuster): " + endOfMonth);

    // 5. TemporalQuery
    System.out.println("\n--- 5. TemporalQuery ---");
    // Use a built-in query to extract the precision
    TemporalUnit precision = accessorObj.query(TemporalQueries.precision());
    System.out.println("Precision of LocalDate: " + precision);

    // Custom TemporalQuery: extract whether the date falls on a weekend
    TemporalQuery<Boolean> isWeekendQuery =
        temporal -> {
          int day = temporal.get(ChronoField.DAY_OF_WEEK);
          return day == DayOfWeek.SATURDAY.getValue() || day == DayOfWeek.SUNDAY.getValue();
        };
    Boolean isWeekend = accessorObj.query(isWeekendQuery);
    System.out.println("Is 2026-09-10 a weekend? " + isWeekend);
  }
}
