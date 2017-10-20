package com.android.robotmap.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2016/11/14.
 */

public class DateUtils {

    /**
     * The tag.
     */
    private static String TAG = "DateUtil";

    /**
     * 时间日期格式化到年月日时分秒.
     */
    public static String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间日期格式化到年月
     */
    public static String dateFormatYMD = "yyyy-MM-dd";

    /**
     * 时间日期格式化到年月
     */
    public static String dateFormatYM = "yyyy-MM";

    /**
     * 时间日期格式化到年月日时
     */
    public static String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

    /**
     * 时间日期格式化到月日
     */
    public static String dateFormatMD = "MM/dd";

    /**
     * 时分
     */
    public static String dateFormatHMS = "HH:mm:ss";

    /**
     * 时分
     */
    public static String dateFormatHM = "HH:mm";

    /**
     * 中文
     */
    public static String dateFormatymdhm = "yyyy年MM月dd  HH时mm";

    /**
     * 描述：String类型的日期时间转化为Date类型.
     *
     * @param strDate String形式的日期时
     * @param format  格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return Date Date类型日期时间
     */
    public static Date getDateByFormat(String strDate, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date date = null;
        try {
            date = mSimpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 描述：获取偏移之后的Date.
     *
     * @param date          日期时间
     * @param calendarField Calendar属，对应offset的，
     *                      (Calendar.DATE,表示+offset,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset        偏移(值大0,表示+,值小0,表示)
     * @return Date 偏移之后的日期时
     */
    public static Date getDateByOffset(Date date, int calendarField, int offset) {
        Calendar c = new GregorianCalendar();
        try {
            c.setTime(date);
            c.add(calendarField, offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 描述：获取指定日期时间的字符(可偏).
     *
     * @param strDate       String形式的日期时
     * @param format        格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @param calendarField Calendar属，对应offset的，
     *                      (Calendar.DATE,表示+offset,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset        偏移(值大0,表示+,值小0,表示)
     * @return String String类型的日期时
     */
    public static String getStringByOffset(String strDate, String format,
                                           int calendarField, int offset) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            c.setTime(mSimpleDateFormat.parse(strDate));
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：Date类型转化为String类型(可偏).
     *
     * @param date          the date
     * @param format        the format
     * @param calendarField the calendar field
     * @param offset        the offset
     * @return String String类型日期时间
     */
    public static String getStringByOffset(Date date, String format,
                                           int calendarField, int offset) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            c.setTime(date);
            c.add(calendarField, offset);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 获取两个日期
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static long getDayMargin(Date beginDate, Date endDate) {
        long margin = 0;
        margin = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);
        return margin;
    }


    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date   the date
     * @param format the format
     * @return String String类型日期时间
     */
    public static String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取指定日期时间的字符,用于导出想要的格
     *
     * @param strDate String形式的日期时间，必须为yyyy-MM-dd HH:mm:ss格式
     * @param format  输出格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return String 转换后的String类型的日期时
     */
    public static String getStringByFormat(String strDate, String format) {
        String mDateTime = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                    dateFormatYMDHMS);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            c.setTime(mSimpleDateFormat.parse(strDate));
            SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat(format);
            mSimpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            mDateTime = mSimpleDateFormat2.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;
    }

    /**
     * 描述：获取milliseconds表示的日期时间的字符
     *
     * @param milliseconds the milliseconds
     * @param format       格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符
     */
    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            thisDateTime = mSimpleDateFormat.format(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 描述：获取表示当前日期时间的字符
     *
     * @param format 格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @return String String类型的当前日期时
     */
    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;

    }

    /**
     * 描述：获取表示当前日期时间的字符(可偏).
     *
     * @param format        格式化字符串，如"yyyy-MM-dd HH:mm:ss"
     * @param calendarField Calendar属，对应offset的，
     *                      (Calendar.DATE,表示+offset,Calendar.HOUR_OF_DAY,表示＋offset小时)
     * @param offset        偏移(值大0,表示+,值小0,表示)
     * @return String String类型的日期时
     */
    public static String getCurrentDateByOffset(String format,
                                                int calendarField, int offset) {
        String mDateTime = null;
        try {
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            Calendar c = new GregorianCalendar();
            c.add(calendarField, offset);
            mDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDateTime;

    }

    /**
     * 描述：计算两个日期所差的天数.
     *
     * @param date1 第一个时间的毫秒表示
     * @param date2 第二个时间的毫秒表示
     * @return int 差的天数
     */
    public static int getOffectDay(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        // 先判断是否同
        int y1 = calendar1.get(Calendar.YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        int d1 = calendar1.get(Calendar.DAY_OF_YEAR);
        int d2 = calendar2.get(Calendar.DAY_OF_YEAR);
        int maxDays = 0;
        int day = 0;
        if (y1 - y2 > 0) {
            maxDays = calendar2.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 + maxDays;
        } else if (y1 - y2 < 0) {
            maxDays = calendar1.getActualMaximum(Calendar.DAY_OF_YEAR);
            day = d1 - d2 - maxDays;
        } else {
            day = d1 - d2;
        }
        return day;
    }

    /**
     * 描述：计算两个日期所差的小时
     *
     * @param date1 第一个时间的毫秒表示
     * @param date2 第二个时间的毫秒表示
     * @return int 差的小时
     */
    public static int getOffectHour(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int h1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int h2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int h = 0;
        int day = getOffectDay(date1, date2);
        h = h1 - h2 + day * 24;
        return h;
    }

    /**
     * 描述：计算两个日期所差的分钟
     *
     * @param date1 第一个时间的毫秒表示
     * @param date2 第二个时间的毫秒表示
     * @return int 差的分钟
     */
    public static int getOffectMinutes(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);
        int m1 = calendar1.get(Calendar.MINUTE);
        int m2 = calendar2.get(Calendar.MINUTE);
        int h = getOffectHour(date1, date2);
        int m = 0;
        m = m1 - m2 + h * 60;
        return m;
    }

    /**
     * 描述：获取本周一.
     *
     * @param format the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.MONDAY);
    }

    /**
     * 描述：获取本周日.
     *
     * @param format the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfWeek(String format) {
        return getDayOfWeek(format, Calendar.SUNDAY);
    }

    /**
     * 描述：获取本周的某一
     *
     * @param format        the format
     * @param calendarField the calendar field
     * @return String String类型日期时间
     */
    private static String getDayOfWeek(String format, int calendarField) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            int week = c.get(Calendar.DAY_OF_WEEK);
            if (week == calendarField) {
                strDate = mSimpleDateFormat.format(c.getTime());
            } else {
                int offectDay = calendarField - week;
                if (calendarField == Calendar.SUNDAY) {
                    offectDay = 7 - Math.abs(offectDay);
                }
                c.add(Calendar.DATE, offectDay);
                strDate = mSimpleDateFormat.format(c.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取本月第
     *
     * @param format the format
     * @return String String类型日期时间
     */
    public static String getFirstDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            // 当前月的第一
            c.set(GregorianCalendar.DAY_OF_MONTH, 1);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;

    }

    /**
     * 描述：获取本月最后一
     *
     * @param format the format
     * @return String String类型日期时间
     */
    public static String getLastDayOfMonth(String format) {
        String strDate = null;
        try {
            Calendar c = new GregorianCalendar();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            // 当前月的后一
            c.set(Calendar.DATE, 1);
            c.roll(Calendar.DATE, -1);
            strDate = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 描述：获取表示当前日期的0点时间毫秒数.
     *
     * @return the first time of day
     */
    public static long getFirstTimeOfDay() {
        Date date = null;
        try {
            String currentDate = getCurrentDate(dateFormatYMD);
            date = getDateByFormat(currentDate + " 00:00:00", dateFormatYMDHMS);
            return date.getTime();
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 描述：获取表示当前日24点时间毫秒数.
     *
     * @return the last time of day
     */
    public static long getLastTimeOfDay() {
        Date date = null;
        try {
            String currentDate = getCurrentDate(dateFormatYMD);
            date = getDateByFormat(currentDate + " 24:00:00", dateFormatYMDHMS);
            return date.getTime();
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 描述：判断是否是闰年()
     * <p/>
     * (year能被4整除 并且 不能100整除) 或 year能被400整除,则该年为闰年.
     *
     * @param year 年代（如2012
     * @return boolean 是否为闰
     */
    public static boolean isLeapYear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 描述：根据时间返回格式化后的时间的描�?. 小于1小时显示多少分钟�? 大于1小时显示今天＋实际日期，大于今天全部显示实际时间
     *
     * @param strDate   the str date
     * @param outFormat the out format
     * @return the string
     */
    public static String formatDateStr2Desc(String strDate, String outFormat) {

        DateFormat df = new SimpleDateFormat(dateFormatYMDHMS);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c2.setTime(df.parse(strDate));
            c1.setTime(new Date());
            int d = getOffectDay(c1.getTimeInMillis(), c2.getTimeInMillis());
            if (d == 0) {
                int h = getOffectHour(c1.getTimeInMillis(),
                        c2.getTimeInMillis());
                if (h > 0) {
                    return "今天" + getStringByFormat(strDate, dateFormatHM);
                    // return h + "小时�?";
                } else if (h < 0) {
                    // return Math.abs(h) + "小时�?";
                } else if (h == 0) {
                    int m = getOffectMinutes(c1.getTimeInMillis(),
                            c2.getTimeInMillis());
                    if (m > 0) {
                        return m + "分钟";
                    } else if (m < 0) {
                        // return Math.abs(m) + "分钟�?";
                    } else {
                        return "刚刚";
                    }
                }

            } else if (d > 0) {
                if (d == 1) {
                    // return "昨天"+getStringByFormat(strDate,outFormat);
                } else if (d == 2) {
                    // return "前天"+getStringByFormat(strDate,outFormat);
                }
            } else if (d < 0) {
                if (d == -1) {
                    // return "明天"+getStringByFormat(strDate,outFormat);
                } else if (d == -2) {
                    // return "后天"+getStringByFormat(strDate,outFormat);
                } else {
                    // return Math.abs(d) +
                    // "天后"+getStringByFormat(strDate,outFormat);
                }
            }

            String out = getStringByFormat(strDate, outFormat);
            if (!TextUtils.isEmpty(out)) {
                return out;
            }
        } catch (Exception e) {
        }

        return strDate;
    }

    /**
     * 取指定日期为星期
     *
     * @param strDate  指定日期
     * @param inFormat 指定日期格式
     * @return String 星期
     */
    public static String getWeekNumber(String strDate, String inFormat) {
        String week = "星期";
        Calendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat(inFormat);
        try {
            calendar.setTime(df.parse(strDate));
        } catch (Exception e) {
            return "错误";
        }
        int intTemp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (intTemp) {
            case 0:
                week = "星期七";
                break;
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * 取指定日期为星期
     *
     * @param strDate  指定日期
     * @param inFormat 指定日期格式
     * @return String 星期
     */
    public static String getWeekName(String strDate, String inFormat) {
        String week = "星期";
        Calendar calendar = new GregorianCalendar();
        DateFormat df = new SimpleDateFormat(inFormat);
        try {
            calendar.setTime(df.parse(strDate));
        } catch (Exception e) {
            return "错误";
        }
        int intTemp = calendar.get(Calendar.DAY_OF_WEEK) + 1;
        switch (intTemp) {
            case 2:
                week = "周二";
                break;
            case 3:
                week = "周三";
                break;
            case 4:
                week = "周四";
                break;
            case 5:
                week = "周五";
                break;
            case 6:
                week = "周六";
                break;
            case 7:
                week = "周日";
                break;
            case 8:
                week = "周一";
                break;
        }
        return week;
    }

    /**
     * 将long转换为String
     *
     * @param dt
     * @return
     */
    public static String Long2SimpleString(long dt) {

        Date date = new Date(dt);

        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月d  HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.format(date);
    }

    public static String Long2SimpleString(long dt, String flag) {

        Date date = new Date(dt);

        SimpleDateFormat format = new SimpleDateFormat(flag);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.format(date);
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        System.out.println(formatDateStr2Desc("2012-3-2 12:2:20",
                "MM月dd  HH:mm"));
    }

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    public static boolean isBeyongNowDate(String tagDate, String formatType) {
        try {
            Long tagLong = stringToLong(tagDate, formatType);
            if (new Date().getTime() - tagLong > 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}
