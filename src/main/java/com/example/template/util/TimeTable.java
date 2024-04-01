package com.example.template.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class TimeTable {
    public static long convertLocalTimeToUTC(long localDateTime) {
        TimeZone z = TimeZone.getDefault();
        // The offset not includes daylight savings time
        //int offset = z.getRawOffset();
        // The offset includes daylight savings time
        int offset = z.getOffset(localDateTime);

        long UTCTime = localDateTime - offset;
        return UTCTime;
    }

    public static long convertUTCToLocalTime(long UTCDateTime) {
        TimeZone z = TimeZone.getDefault();
        // The offset not includes daylight savings time
        //int offset = z.getRawOffset();
        // The offset includes daylight savings time
        int offset = z.getOffset(UTCDateTime);

        long localDateTime = UTCDateTime + offset;
        return localDateTime;
    }

    public static LocalDateTime convertMilliSecondsToLocalDateTime(long milliSeconds) {
        return Instant
                .ofEpochMilli(milliSeconds)
                // .atZone(ZoneId.of("UTC"))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static long convertLocalDateTimeToMilliSeconds(LocalDateTime localDateTime) {
        return localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }
}
