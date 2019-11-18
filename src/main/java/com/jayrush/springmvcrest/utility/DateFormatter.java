package com.jayrush.springmvcrest.utility;


import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    static SimpleDateFormat dateField7 = new SimpleDateFormat("MMddHHmmss");
    static SimpleDateFormat dateField12 = new SimpleDateFormat("HHmmss");
    static SimpleDateFormat dateField13 = new SimpleDateFormat("MMdd");
    static SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMddhhmmssSS");
    static SimpleDateFormat interDate = new SimpleDateFormat("dd MMM yyyy");


    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static String date(Date date){

        return dateFormat.format(date);
    }

    public static String requestID(Date date){

        return dateField7.format(date);
    }

    public static String uniqueId(Date date){

        return timeStamp.format(date);
    }

    public static String timeStamp(Date date){

        return dateFormatter.format(date);
    }

    public static String field7(Date date){

        return dateField7.format(date);
    }

    public static String field12(Date date){

        return dateField12.format(date);
    }

    public static String field13(Date date){

        return dateField13.format(date);
    }

    public static String createdOn(Date date){

        return dateFormatter.format(date);
    }


    public static String responseField7(String date) {
        Date dateResponse = new Date();
        try {
             dateResponse = dateFormat.parse(date);
            System.out.println("DATE IS @@@@@@ {}" + dateResponse);
        }catch (ParseException e){

        }
        return format.format(dateResponse);
    }

    public static String accopening(String date) {
        String newDate = "";
        try {
            Date dateResponse = dateFormat.parse(date);
            System.out.println("DATE IS @@@@@@ {}" + dateResponse);
            newDate = format.format(dateResponse);
        }catch (ParseException e){
        }
        return newDate;
    }

    public static String interDate(Date date){

        return interDate.format(date);
    }
}
