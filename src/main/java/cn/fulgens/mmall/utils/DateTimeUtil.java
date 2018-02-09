package cn.fulgens.mmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
* @Author: fulgens
* @Description: 使用joda-time创建的时间日期转换工具类
* @Date: Created in 2018/2/6 21:14
* @Modified by:
*/
public class DateTimeUtil {

    public static final String STANDARD_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr, String formatPattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatPattern);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    public static Date strToDate(String dateTimeStr) {
        return strToDate(dateTimeStr, STANDARD_PATTERN);
    }

    public static String dateToStr(Date date, String formatPattern) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatPattern);
    }

    public static String dateToStr(Date date) {
        return dateToStr(date, STANDARD_PATTERN);
    }
}
