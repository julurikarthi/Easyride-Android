package com.easyride.driverapp.Adapters;

import android.content.Context;

public class ApplicationContextProvider {
    private static Context applicationContext;

    public static void setApplicationContext(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }
}
