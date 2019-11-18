package com.jayrush.springmvcrest.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date dateFullFormat(String date) {
        Date date1 = null;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }
}
