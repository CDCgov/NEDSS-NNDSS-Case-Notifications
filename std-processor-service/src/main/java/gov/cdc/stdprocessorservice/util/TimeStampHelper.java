package gov.cdc.stdprocessorservice.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeStampHelper {
    private TimeStampHelper() {

    }
    public static Timestamp getCurrentTimeStamp(String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime gmt = zdt.withZoneSameInstant(zoneId);
        return Timestamp.valueOf(gmt.toLocalDateTime());
    }

}
