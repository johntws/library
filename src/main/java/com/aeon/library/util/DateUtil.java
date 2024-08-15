package com.aeon.library.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    public static Timestamp stringToTimestamp(String str, SimpleDateFormat formatter) {
        Date parsedDate;

        try {
            parsedDate = formatter.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return new Timestamp(parsedDate.getTime());
    }

}
