package com.ubaby.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author AlbertRui
 * @date 2018-05-07 14:57
 */
@SuppressWarnings("JavaDoc")
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转日期
     *
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatStr) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);

        return dateTime.toDate();

    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr) {

        if (date == null)
            return StringUtils.EMPTY;

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);

    }

    /**
     * 字符串转日期
     *
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);

        return dateTime.toDate();

    }

    /**
     * 日期转字符串
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {

        if (date == null)
            return StringUtils.EMPTY;

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);

    }

}
