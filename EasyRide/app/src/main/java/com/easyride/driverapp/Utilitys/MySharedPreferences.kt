package com.easyride.driverapp.Utilitys

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val PREFS_FILENAME = "easyRiceSharedPreference"
        private const val KEY_NAME = "name"
        private const val KEY_AGE = "age"
    }

    fun save(key: String, name: String) {
        editor.putString(key, name)
        editor.apply()
    }

    fun saveAge(age: Int) {
        editor.putInt(KEY_AGE, age)
        editor.apply()
    }

    fun getName(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun getAge(): Int {
        return sharedPreferences.getInt(KEY_AGE, -1)
    }
}
