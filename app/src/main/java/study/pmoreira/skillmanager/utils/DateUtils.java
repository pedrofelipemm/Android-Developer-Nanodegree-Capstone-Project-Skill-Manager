package study.pmoreira.skillmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";

    private DateUtils() {}

    public static String format(int dayOfMonth, int monthOfYear, int year) {
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.US)
                .format(newCalendar(dayOfMonth, monthOfYear, year).getTime());
    }

    public static String format(Date date) {
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.US).format(date);
    }

    /**
     * @param date String date format: {@link DateUtils#DEFAULT_DATE_PATTERN}
     */
    public static Date parse(String date) throws ParseException {
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.US).parse(date);
    }

    private static Calendar newCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(0, 0, 0);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

    private static Calendar newCalendar(int dayOfMonth, int monthOfYear, int year) {
        Calendar c = newCalendar();
        c.set(year, monthOfYear, dayOfMonth);

        return c;
    }

}
