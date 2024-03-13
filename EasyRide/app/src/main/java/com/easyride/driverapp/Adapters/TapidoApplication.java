package com.easyride.driverapp.Adapters;


import android.app.Application;

public class TapidoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContextProvider.setApplicationContext(this);
    }
}
