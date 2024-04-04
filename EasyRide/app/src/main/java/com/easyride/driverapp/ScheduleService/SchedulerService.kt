package com.easyride.driverapp.ScheduleService

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

const val SchedulerServiceEighthoursOffline = "SchedulerServiceEighthoursOffline"

class SchedulerService {

    fun schedule(context: Context) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AppService::class.java).let { intent ->
            intent.action = SchedulerServiceEighthoursOffline
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Set the alarm to start at 8-hour intervals
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.HOUR, 8)

        // Schedule the alarm
        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            8 * 60 * 60 * 1000, // 8 hours in milliseconds
            alarmIntent
        )
    }
}