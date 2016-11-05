package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by Misha on 11/5/2016.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setLocaleFa (Context context){
        Locale locale = new Locale("fa");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }

    public static void setLocaleEn (Context context){
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }
}
