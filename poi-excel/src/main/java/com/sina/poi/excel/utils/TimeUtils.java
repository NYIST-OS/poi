package com.sina.poi.excel.utils;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @ClassName TimeUtils
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:29
 * @Version 1.0
 */
public abstract class TimeUtils {

    public static final ZoneId ZONE8 = ZoneId.of("+08:00");

    public static final TimeZone TIME_ZONE8 = TimeZone.getTimeZone(ZONE8);

    public static final ZoneOffset ZONE_OFFSET8 = ZoneOffset.of("+08:00");


    public static final String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "uuuu-MM-dd";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String CLOSE_DATE_FORMAT = "uuuuMMdd";

    public static final String CLOSE_DATE_TIME_FORMAT = "uuuuMMddHHmmss";

    public static final String FULL_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";

    public static final String ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ssZ";

    public static final String FULL_ZONE_DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss.SSSZ";

    public static final String LOCALE_ZONE_DATE_TIME_FORMAT = "E MMM dd HH:mm:ss Z uuuu";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_FORMAT);

    public static final DateTimeFormatter CLOSE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CLOSE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter FULL_ZONE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_ZONE_DATE_TIME_FORMAT);

    public static final DateTimeFormatter ENGLISH_ZONE_FORMATTER =
            DateTimeFormatter.ofPattern(LOCALE_ZONE_DATE_TIME_FORMAT, Locale.ENGLISH);

}
