package my.messenger.androidclient.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateService {
    private static final Calendar myCalendar = Calendar.getInstance();
    private static final String myFormat = "MM/dd/yyyy"; //"yyyy-MM-dd"; //In which you need put here
    private static final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    private static final String myTimeDtFormat = "hh:mm a"; //"hh:mm:ss yyyy-MM-dd"; //In which you need put here
    private static final SimpleDateFormat sdfTimeDt = new SimpleDateFormat(myTimeDtFormat, Locale.US);

    public static boolean isValidDate(String s) {
        return parse(s) != null;
    }

    public static Date parse(String s) {
        Date d;
        try {
            d = sdf.parse(s);
            return d;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toDateString(Date d) {
        if (d == null)
            return null;
        return sdf.format(d);
    }

    public static String toShortDateTimeString(Date d) {
        if (d == null)
            return null;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(d);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        if (sameDay)
            return sdfTimeDt.format(d);

        return sdf.format(d);
    }
}
