package com.haiphamcoder.reporting.shared;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtils {

    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long convertLocalDateTimeToEpoch(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toEpochSecond();
    }

    public static long convertLocalDateTimeToUtcEpoch(LocalDateTime localDateTime) {
        return convertLocalDateTimeToEpoch(localDateTime, ZoneId.of("UTC"));
    }
}
