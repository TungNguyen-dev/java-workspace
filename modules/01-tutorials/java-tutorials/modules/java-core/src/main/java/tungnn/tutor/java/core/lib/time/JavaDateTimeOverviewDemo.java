package tungnn.tutor.java.core.lib.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Lớp minh họa các kiểu đại diện thời gian (Temporal) và khoảng thời gian (TemporalAmount) phổ biến
 * nhất trong Java Date-Time API (java.time).
 */
public class JavaDateTimeOverviewDemo {

  static void main() {
    System.out.println("=== 1. POINT IN TIME (Mốc thời gian tuyệt đối) ===");
    // Thời điểm tuyệt đối theo giờ UTC (epoch timestamp)
    Instant currentInstant = Instant.now();
    System.out.println("Instant (UTC)            : " + currentInstant);

    System.out.println("\n=== 2. HUMAN DATE-TIME (Thời gian theo góc nhìn con người) ===");
    // Ngày (không giờ, không múi giờ)
    LocalDate currentDate = LocalDate.now();
    System.out.println("LocalDate                : " + currentDate);

    // Ngày và Giờ (không múi giờ)
    LocalDateTime currentDateTime = LocalDateTime.now();
    System.out.println("LocalDateTime            : " + currentDateTime);

    // Ngày, Giờ kèm thông tin Múi giờ
    ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    System.out.println("ZonedDateTime (ICT)      : " + currentZonedDateTime);

    System.out.println("\n=== 3. TEMPORAL AMOUNT (Khoảng thời gian) ===");
    // Period: Khoảng thời gian theo đơn vị Ngày - Tháng - Năm (Date-based)
    Period zeroDaysPeriod = Period.ofDays(0);
    Period oneWeekPeriod = Period.ofWeeks(1);
    System.out.println("Period (0 days)          : " + zeroDaysPeriod);
    System.out.println("Period (1 week)          : " + oneWeekPeriod);

    // Duration: Khoảng thời gian theo đơn vị Giờ - Phút - Giây - Mắt (Time-based / Exact time)
    Duration zeroMillisDuration = Duration.of(0, ChronoUnit.MILLIS);
    Duration sixtySecondsDuration = Duration.of(60, ChronoUnit.SECONDS);
    System.out.println("Duration (0 ms)          : " + zeroMillisDuration);
    System.out.println("Duration (60 seconds)    : " + sixtySecondsDuration);
  }
}
