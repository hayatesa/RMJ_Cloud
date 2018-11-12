/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package rmj.cloud.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm", "yyyy.MM" };

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 描述:获取下一个月
     *
     * @return月份值
     */
    public static String getNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
        String preMonth = dft.format(cal.getTime());
        return preMonth;
    }

    /**
     * 描述:获取下下一个月
     *
     * @return 月份值
     */
    public static String getNextNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 2);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
        String preMonth = dft.format(cal.getTime());
        return preMonth;
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
     * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * 获取最近几年年份
     *
     * @return
     */
    public static List<Integer> getRecentYears() {
        String recentYear = getYear();
        List<Integer> recentYears = new ArrayList<Integer>();
        recentYears.add(Integer.parseInt(recentYear) - 2);
        recentYears.add(Integer.parseInt(recentYear) - 1);
        recentYears.add(Integer.parseInt(recentYear));
        return recentYears;
    }

    /**
     * 获取一年总月份
     *
     * @return
     */
    public static List<Integer> getMonthsOfYear() {
        List<Integer> months = new ArrayList<Integer>();
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }

        return months;
    }

    public static String getPropertyFeeMonth(Integer billingDate) {
        // 例如物业月结日为每月5号。
        // 1. 用户1月4日使用积分或第三方支付物业费，即支付2月的物业费。
        // 2. 用户1月5日-2月4日期间支付， 即支付3月的物业费
        // 月结日
        String propertyFeeMonth = null;
        if (new Integer(DateUtils.getDay()) < billingDate) {
            propertyFeeMonth = DateUtils.getNextMonth();
        } else {
            propertyFeeMonth = DateUtils.getNextNextMonth();
        }
        return propertyFeeMonth;
    }

    public static boolean isDateExpired(Date date, int minutes) {
        return date.getTime() + (1000L * 60 * minutes) < System.currentTimeMillis();
    }

    public static Date getNextFewDays(Date currentDate, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static String covertDateToyyyymm(Date dateTime) {
        LocalDate date = getLocalDateByDate(dateTime);
        int monthValue = date.getMonthValue();
        // 是否补0
        String month = monthValue < 10 ? "0" + monthValue : monthValue + "";
        return date.getYear() + month;
    }

    public static String covertDateToyyyymmdd(Date dateTime) {
        LocalDate date = getLocalDateByDate(dateTime);
        int monthValue = date.getMonthValue();
        // 是否补0
        String month = monthValue < 10 ? "0" + monthValue : monthValue + "";
        return date.getYear() + month + date.getDayOfMonth();
    }

    public static LocalDate getLocalDateByDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date getDateByLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param yyyymm
     * @return yyyy-mm
     */
    public static String covertyyyymmToyyyyDashmm(String yyyymm) {
        int len = yyyymm.length();
        if (len == 6) {
            String yyyy = yyyymm.substring(0, 4);
            String mm = yyyymm.substring(4, len);
            return yyyy + " - " + mm;
        }
        return "";
    }

    /**
     * 获取当月第一天开始时刻
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
        return Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当月最后一天开始时刻
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        return Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取一天的开始时刻
     *
     * @param date
     * @return
     */
    public static Date getStartTimeOfDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime localDateTime = localDate.atTime(0, 0, 0);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取一天的最后时刻
     *
     * @param date
     * @return
     */
    public static Date getEndTimeOfDay(Date date) {
        if (date == null)
            return null;
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime localDateTime = localDate.atTime(23, 59, 59);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @param args
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException {
        // LOG.debug(formatDate(parseDate("2010/3/6")));
        // LOG.debug(getDate("yyyy年MM月dd日 E"));
        // long time = new Date().getTime()-parseDate("2012-11-19").getTime();
        // LOG.debug(time/(24*60*60*1000));
        System.out.println(getNextMonth());
        System.out.println(getNextNextMonth());
        System.out.println(getDate("yyyy年MM月dd日"));

        String year = "2012-11".substring(0, 4);
        String month = "2012-11".substring("2012-11".length() - 2, "2012-11".length());
        System.out.println(year);
        System.out.println(month);

        Date date = getLastDayOfMonth(new Date());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        getEndTimeOfDay(date);
        System.out.println(date);
    }

}
