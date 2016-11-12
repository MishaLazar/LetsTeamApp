package com.misha.mor.letsteamapp.letsteamapp;
/**
 * Created by Misha on 9/10/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilMethods {



    public final static String getTimeStamp(){
        Calendar calendar = Calendar.getInstance();
        String currentTimestamp = new Timestamp(calendar.getTime().getTime()).toString();
        return currentTimestamp;
    }

    public final static String getDateTime() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
        return sdf.format(new Date());
    }

    public final static String getDateTimeSimple() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
        return sdf.format(new Date());
    }
    public final static String getDateSimple() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date());
    }
    public final static String getTimeSimple() {
        // get date time in custom format
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public static boolean containsIgnoreCase( String haystack, String needle ) {
        if(needle.equals(""))
            return true;
        if(haystack == null || needle == null || haystack .equals(""))
            return false;

        Pattern p = Pattern.compile(needle,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
        Matcher m = p.matcher(haystack);
        return m.find();
    }

    /*public void convertHashmapIntoArrayLit(HashMap<?,?> hashMap){
        // Converting HashMap Values into ArrayList
        List<?> valueList = new ArrayList<>(hashMap.values());
        System.out.println("\n==> Size of Value list: " + valueList.size());
        for (Objecttemp : valueList) {
            System.out.println(temp);
        }

        List<Entry> entryList = new ArrayList<Entry>(companyDetails.entrySet());
        System.out.println("\n==> Size of Entry list: " + entryList.size());
        for (Entry temp : entryList) {
            System.out.println(temp);
        }
    }*/

}