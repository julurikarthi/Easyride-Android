package com.easyride.driverapp.Utilitys

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.easyride.driverapp.Adapters.ApplicationContextProvider



object Constants {
    val driverId = "driverId"
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
    }

}