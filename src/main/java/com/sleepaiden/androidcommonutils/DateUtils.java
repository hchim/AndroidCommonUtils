package com.sleepaiden.androidcommonutils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by huiche on 6/10/17.
 */

public class DateUtils {
    private static DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);

    public static String dateToStr(Date date) {
        return dateFormat.format(date);
    }

    public static Date strToDate(String str) {
        try {
            Date time = dateFormat.parse(str);
            return time;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToStr(Date date, String format) {
        return dateToStr(date, format, null);
    }

    public static String dateToStr(Date date, String format, String timezone) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        if (timezone != null) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return dateFormat.format(date);
    }

    public static Date strToDate(String str, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date time = dateFormat.parse(str);
            return time;
        } catch (ParseException e) {
            return null;
        }
    }

    public static int yearsBetween(Calendar from, Calendar to) {
        int year = from.get(Calendar.YEAR);
        int years = 0;

        do {
            from.add(Calendar.YEAR, 1);
            if (from.before(to)) {
                years++;
            } else {
                break;
            }
        } while (true);

        from.set(Calendar.YEAR, year);
        return years;
    }

    public static int monthsBetween(Calendar from, Calendar to) {
        int year = from.get(Calendar.YEAR);
        int month = from.get(Calendar.MONTH);
        int months = 0;

        do {
            from.add(Calendar.MONTH, 1);
            if (from.before(to)) {
                months++;
            } else {
                break;
            }
        } while (true);

        from.set(Calendar.YEAR, year);
        from.set(Calendar.MONTH, month);
        return months;
    }

    public static int daysBetween(Calendar from, Calendar to) {
        long toTime = to.getTimeInMillis();
        long fromTime = from.getTimeInMillis();

        if (toTime < fromTime) {
            return 0;
        }

        return (int) ((toTime - fromTime) / 1000/ 60/ 60/ 24);
    }

    public static boolean before(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) {
            return true;
        }

        if (cal1.get(Calendar.MONTH) < cal2.get(Calendar.MONTH)) {
            return true;
        }

        if (cal1.get(Calendar.DATE) < cal2.get(Calendar.DATE)) {
            return true;
        }

        return false;
    }

    public static boolean after(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) {
            return true;
        }

        if (cal1.get(Calendar.MONTH) > cal2.get(Calendar.MONTH)) {
            return true;
        }

        if (cal1.get(Calendar.DATE) > cal2.get(Calendar.DATE)) {
            return true;
        }

        return false;
    }

    public static boolean sameDay(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE)) {
            return true;
        }

        return false;
    }

    /**
     * Return ID of local timezone and replace forward slash with -.
     * @return
     */
    public static String getLocalTimezone(boolean encode) {
        TimeZone timeZone = TimeZone.getDefault();
        if (encode) {
            return timeZone.getID().replace('/', '-');
        }
        return timeZone.getID();
    }

    /**
     * @param date
     * @return true if the date is Monday
     */
    public static boolean isMonday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }

    /**
     * @param date
     * @return true if it is the first day of a month
     */
    public static boolean isFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int d = calendar.get(Calendar.DATE);
        return d == 1;
    }
}
