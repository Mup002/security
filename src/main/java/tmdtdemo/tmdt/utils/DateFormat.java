package tmdtdemo.tmdt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormat {
    public static String DateFormatWithLocate(String expiration){
        java.text.DateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        // dịnh dạng của chuỗi ngày giờ đầu ra
        java.text.DateFormat outputDateFormat = new SimpleDateFormat("HH:mm MM/dd/yyyy");
        //  them mui giờ việt nam
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String outputDateString = null;
        try {
            // chuyển đổi chuỗi ngày giờ vào đối tượng Date
            Date date = inputDateFormat.parse(expiration);
            // chuyển đổi đối tượng Date thành chuỗi ngày giờ mới
            outputDateString = outputDateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDateString;
    }
}
