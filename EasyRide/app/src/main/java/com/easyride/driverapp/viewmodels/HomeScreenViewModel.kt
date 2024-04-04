package com.easyride.driverapp.viewmodels

import com.easyride.driverapp.Adapters.TapidoApplication
import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.easyride.driverapp.Utilitys.MySharedPreferences
import org.json.JSONObject

class HomeScreenViewModel {

    fun registeredforNotification(token: String) {
        val pref = MySharedPreferences(TapidoApplication.getAppContext())
        val driverId = pref.getDriverInfo()?.driverID
        var jsonObject = JSONObject()
        jsonObject.put("driverId", driverId)
        jsonObject.put("notificationToken", token)
        jsonObject.put("platform", "android")
        NetWorkManagers.getInstance().postRequest(RequestMethodType.updatedrivertableNotification,jsonObject,
            object :
            NetWorkManagers.CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                val token = result?.getString("token") ?: ""
                pref.save("notificationToken", token)
                pref.saveBoolean("registeredToken", true)
            }

            override fun onError(errorMessage: String?) {
                val pref = MySharedPreferences(TapidoApplication.getAppContext())
                pref.saveBoolean("registeredToken", false)
            }
        })

    }
}