package com.personal.kopmorning.global.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static String convertUtcToKst(String utcDateStr) {
        ZonedDateTime utc = ZonedDateTime.parse(utcDateStr);
        ZonedDateTime kst = utc.withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        return kst.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
