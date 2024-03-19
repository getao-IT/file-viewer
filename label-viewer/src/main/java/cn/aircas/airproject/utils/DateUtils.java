package cn.aircas.airproject.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DateUtils {

    private static Calendar calendar;

    private static Calendar staiticCalendar;


    /**
     * 返回days天前的时间
     * @param date 基准时间
     * @param days 范围天数
     * @return 基准时间范围days天数的时间
     * @throws ParseException
     */
    public static Date getDayBeforDate(Date date, int days) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date(calendar.getTimeInMillis()));
        Date parseDate = null;
        try {
            parseDate = dateFormat.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseDate;
    }


    /**
     * 返回hours小时前的时间
     * @param date 基准时间
     * @param hours 范围天数
     * @return 基准时间前hours小时的时间
     * @throws ParseException
     */
    public static Date getHoursBeforDate(Date date, int hours) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date(calendar.getTimeInMillis()));
        Date parseDate = null;
        try {
            parseDate = dateFormat.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parseDate;
    }


    /**
     * 返回当前时间未来days天的时间
     * @param days 范围天数
     * @return 基准时间范围days天数的时间
     * @throws ParseException
     */
    public static Date getAffterDate(int days) {
        staiticCalendar = Calendar.getInstance();
        staiticCalendar.setTimeInMillis(new Date().getTime());
        staiticCalendar.add(Calendar.DAY_OF_YEAR, days);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date(staiticCalendar.getTimeInMillis()));
        Date parseDate = null;
        try {
            parseDate = dateFormat.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parseDate;
    }


    /**
     * 获取指定参数的明天的0点时间
     * @param date
     * @return
     */
    public static Date getTomorrowStartTime(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


    /**
     * 获取指定时间的当天最后时间
     * @param date
     * @return
     */
    public static Date getDayLastTime(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }


    /**
     * 获取本周最后时刻
     * @param date
     * @return
     */
    public static Date getWeekLastTime(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }


    /**
     * 获取指定时间的当月最后时刻
     * @param date
     * @return
     */
    public static Date getMonthLastTime(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

        return calendar.getTime();
    }


    /**
     * 获取指定时间的下一个月的开始时刻
     * @param date
     * @return
     */
    public static Date getNextMonthStartTime(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


    /**
     * 根据参数返回固定格式的日期
     * @param date
     * @param standard
     * @return
     * @throws ParseException
     */
    public static String getValidDate(Date date, String standard) throws ParseException {
        SimpleDateFormat dateFormat = null;
        if (standard.toUpperCase().equals("DAY")) { // 筛选维度
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (standard.toUpperCase().equals("WEEK")) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (standard.toUpperCase().equals("MONTH")) {
            dateFormat = new SimpleDateFormat("yyyy-MM");
        }

        return dateFormat.format(date);
    }


    /**
     * 返回当前时间是第几周
     * @param date
     * @return
     */
    public static int getCurrentTimeWeekNum(Date date) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int i = calendar.get(Calendar.WEEK_OF_MONTH);
        return i;
    }
}
