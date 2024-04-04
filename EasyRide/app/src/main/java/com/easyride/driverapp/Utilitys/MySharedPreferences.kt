package com.easyride.driverapp.Utilitys

import android.content.Context
import android.content.SharedPreferences
import com.easyride.driverapp.Driver
import com.google.gson.Gson

class MySharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()
    companion object {
        private const val PREFS_FILENAME = "easyRiceSharedPreference"
        private const val KEY_NAME = "name"
        private const val KEY_AGE = "age"
        private const val Driver_info = "driver_info"
    }

    fun save(key: String, name: String) {
        editor.putString(key, name)
        editor.apply()
    }
    fun saveBoolean(key: String, name: Boolean) {
        editor.putBoolean(key, name)
        editor.apply()
    }

    fun saveAge(age: Int) {
        editor.putInt(KEY_AGE, age)
        editor.apply()
    }

    fun saveDriverInfo(myClass: Driver) {
        val jsonString = gson.toJson(myClass)
        sharedPreferences.edit().putString(Driver_info, jsonString).apply()
    }

    fun getDriverInfo(): Driver? {
        val jsonString = sharedPreferences.getString(Driver_info, null)
        return gson.fromJson(jsonString, Driver::class.java)
    }


    fun getName(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getBoolean(key: String): Boolean? {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getAge(): Int {
        return sharedPreferences.getInt(KEY_AGE, -1)
    }
}
