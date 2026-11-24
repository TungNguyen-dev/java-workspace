package tungnn.tutor.java.core.lib.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class JavaDateTimeOverviewDemo {

    static void main() {
        System.out.println("=== 1. POINT IN TIME (absolute instant) ===");
        // Absolute instant in UTC (epoch timestamp)
        Instant currentInstant = Instant.now();
        System.out.println("Instant (UTC)            : " + currentInstant);

        System.out.println("\n=== 2. HUMAN DATE-TIME (time as people see it) ===");
        // Date (no time, no time zone)
        LocalDate currentDate = LocalDate.now();
        System.out.println("LocalDate                : " + currentDate);

        // Date and time (no time zone)
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("LocalDateTime            : " + currentDateTime);

        // Date and time with time zone information
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        System.out.println("ZonedDateTime (ICT)      : " + currentZonedDateTime);

        System.out.println("\n=== 3. TEMPORAL AMOUNT (amount of time) ===");
        // Period: an amount of time in days - months - years (date-based)
        Period zeroDaysPeriod = Period.ofDays(0);
        Period oneWeekPeriod = Period.ofWeeks(1);
        System.out.println("Period (0 days)          : " + zeroDaysPeriod);
        System.out.println("Period (1 week)          : " + oneWeekPeriod);

        // Duration: an amount of time in hours - minutes - seconds - nanos (time-based / exact time)
        Duration zeroMillisDuration = Duration.of(0, ChronoUnit.MILLIS);
        Duration sixtySecondsDuration = Duration.of(60, ChronoUnit.SECONDS);
        System.out.println("Duration (0 ms)          : " + zeroMillisDuration);
        System.out.println("Duration (60 seconds)    : " + sixtySecondsDuration);
    }
}
