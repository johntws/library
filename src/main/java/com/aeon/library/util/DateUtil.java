package com.aeon.library.util;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class DateUtil {

    public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    public static Date stringToDate(String str, SimpleDateFormat formatter) {
        Date parsedDate;

        try {
            parsedDate = new Date(formatter.parse(str).toInstant().toEpochMilli());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return parsedDate;
    }

}
