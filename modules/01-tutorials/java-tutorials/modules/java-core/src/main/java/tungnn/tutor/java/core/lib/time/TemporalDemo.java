package tungnn.tutor.java.core.lib.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.*;

public class TemporalDemo {

  static void main(String[] args) {

    // 1. Temporal & TemporalAccessor
    // LocalDate triển khai Temporal (mà Temporal kế thừa từ TemporalAccessor)
    Temporal temporalObj = LocalDate.of(2026, 9, 10);
    // Read-only view
    TemporalAccessor accessorObj = temporalObj;

    System.out.println("--- 1. Read values (TemporalAccessor) ---");
    // Read-only access: Đọc giá trị thông qua TemporalField
    int year = accessorObj.get(ChronoField.YEAR);
    int dayOfWeek = accessorObj.get(ChronoField.DAY_OF_WEEK);
    System.out.println("Year: " + year + ", Day of Week (1-7): " + dayOfWeek);

    // 2. TemporalField
    System.out.println("\n--- 2. TemporalField ---");
    // Interrogate hoặc kiểm tra xem một Temporal object có hỗ trợ Field nào không
    TemporalField dayOfMonthField = ChronoField.DAY_OF_MONTH;
    if (temporalObj.isSupported(dayOfMonthField)) {
      System.out.println("Day of month supported value: " + temporalObj.get(dayOfMonthField));
    }

    // 3. TemporalUnit & TemporalAmount
    System.out.println("\n--- 3. TemporalUnit & TemporalAmount ---");
    TemporalUnit daysUnit = ChronoUnit.DAYS;
    // Period triển khai TemporalAmount
    TemporalAmount periodTwoWeeks = Period.ofWeeks(2);

    // Cộng thời gian bằng TemporalUnit hoặc TemporalAmount
    Temporal futureDate1 = temporalObj.plus(10, daysUnit);
    Temporal futureDate2 = temporalObj.plus(periodTwoWeeks);
    System.out.println("+ 10 days (via TemporalUnit): " + futureDate1);
    System.out.println("+ 2 weeks (via TemporalAmount): " + futureDate2);

    // 4. TemporalAdjuster
    System.out.println("\n--- 4. TemporalAdjuster ---");
    // Dùng Adjuster có sẵn để điều chỉnh ngày về Friday tiếp theo
    TemporalAdjuster nextFridayAdjuster = TemporalAdjusters.next(DayOfWeek.FRIDAY);
    Temporal nextFriday = temporalObj.with(nextFridayAdjuster);

    // Custom TemporalAdjuster: Tự tạo logic chuyển ngày về cuối tháng
    TemporalAdjuster lastDayOfMonth =
        temporal ->
            temporal.with(
                ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
    Temporal endOfMonth = temporalObj.with(lastDayOfMonth);

    System.out.println("Next Friday: " + nextFriday);
    System.out.println("Last day of month (Custom Adjuster): " + endOfMonth);

    // 5. TemporalQuery
    System.out.println("\n--- 5. TemporalQuery ---");
    // Dùng Query có sẵn để trích xuất Precision
    TemporalUnit precision = accessorObj.query(TemporalQueries.precision());
    System.out.println("Precision of LocalDate: " + precision);

    // Custom TemporalQuery: Trích xuất thông tin xem ngày có phải là Cuối tuần hay không
    TemporalQuery<Boolean> isWeekendQuery =
        temporal -> {
          int day = temporal.get(ChronoField.DAY_OF_WEEK);
          return day == DayOfWeek.SATURDAY.getValue() || day == DayOfWeek.SUNDAY.getValue();
        };
    Boolean isWeekend = accessorObj.query(isWeekendQuery);
    System.out.println("Is 2026-09-10 a weekend? " + isWeekend);
  }
}
