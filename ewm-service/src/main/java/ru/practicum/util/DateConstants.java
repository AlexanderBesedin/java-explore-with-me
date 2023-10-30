package ru.practicum.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateConstants {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter getDefaultFormatter() {
        return DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    public static LocalDateTime getMaxDT() {
        return LocalDateTime.parse("9999-12-31T23:59:59.999999");
    }

    public static LocalDateTime getMinDT() {
        return LocalDateTime.parse("2000-01-01T00:00:00.000000");
    }
}
