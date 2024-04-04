package com.easyride.driverapp.Adapters;


import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

public class TapidoApplication extends Application {

    private static TapidoApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        instance = this;
    }
    public static TapidoApplication getInstance() {
        return instance;
    }
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
