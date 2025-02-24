package ru.job4j.converter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConverterDateTime {

    public static final String FORMAT_PATTERN = "MM.dd.yyyy HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT_PATTERN);
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT_PATTERN);

    public static SimpleDateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    public static Date getDate(LocalDateTime localDateTime) {
        return localDateTime != null ? Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return date != null ? date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime() : null;
    }

    public static LocalDateTime getLocalDateTimeFromString(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, FORMATTER);
        return localDateTime;
    }
}
