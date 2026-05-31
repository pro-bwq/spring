package com.bwq.framework.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bwq
 * @date 2026-05-31 15:25:37
 * @description 日期工具类
 */

public class DateUtils {

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(PATTERN_DATETIME));
    }

    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(PATTERN_DATE));
    }
}
