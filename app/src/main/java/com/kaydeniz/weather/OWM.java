package com.kaydeniz.weather;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OWM {

    public static final String APP_ID="39926ed841202518c1e98da44002be8b";
    public static Location currentLocation=null;


    public static String convertUnixToDate(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm EEE MM yyyy");
        String formatted= sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        String formatted= sdf.format(date);
        return formatted;
    }
}
