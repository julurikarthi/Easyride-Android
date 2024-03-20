package com.easyride.driverapp.Utilitys

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import com.easyride.driverapp.Adapters.ApplicationContextProvider



object Constants {
    val driverId = "driverId"
    var phonNumber = "phonNumber"
}

class Preferences {


    companion object {
        var driverId = Preferences.getInstance().getString(Constants.driverId, "")

        // Function to get the singleton instance
        fun getInstance(): SharedPreferences {
             val PREFS_FILENAME = "easyRiceSharedPreference"
            val applicationContext = ApplicationContextProvider.getApplicationContext()
            val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
            return sharedPreferences
        }

        fun getInstance(context: ComponentActivity): SharedPreferences {
            val PREFS_FILENAME = "easyRiceSharedPreference"
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
            return sharedPreferences
        }

    }
}