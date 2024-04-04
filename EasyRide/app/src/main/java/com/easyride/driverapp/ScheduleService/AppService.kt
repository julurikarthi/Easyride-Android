package com.easyride.driverapp.ScheduleService

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.viewmodels.HomeFragmentViewModel

class AppService: BroadcastReceiver() {
    var viewmodel = HomeFragmentViewModel()
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == SchedulerServiceEighthoursOffline) {
            val pref = context?.let { MySharedPreferences(it) }
            val driverid = pref?.getDriverInfo()?.driverID ?: ""
            val activity = getActivityFromContext(context)
            activity?.let {
                val driverStatus = pref?.saveBoolean("driverStatus", false) ?: false
                if (driverStatus == true) {
                    viewmodel.updateDriverStatus(it, driverId = driverid,"","","false","false","","")
                }
            }
        }
    }

    fun getActivityFromContext(context: Context?): Activity? {
        if (context == null) return null

        if (context is Activity) {
            // Context is an activity
            return context
        } else if (context is ContextWrapper) {
            // Context is a wrapper, unwrap it recursively
            return getActivityFromContext(context.baseContext)
        }

        // Context is neither an activity nor a context wrapper
        return null
    }
}