package ru.practicum.util;

import java.time.LocalDateTime;

public class DateConstant {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime getMaxDateTime() {
        return LocalDateTime.parse("9999-12-31T23:59:59.999999");
    }

    public static LocalDateTime getMinDateTime() {
        return LocalDateTime.parse("2000-01-01T00:00:00.000000");
    }
}
