package com.easyride.driverapp.viewmodels

import androidx.activity.ComponentActivity
import com.easyride.driverapp.NetWorkManager.NetWorkCallsManagers
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import org.json.JSONObject

interface  MainActivityProtocol {
    fun onSuccess(result: JSONObject?)
    fun onFailure(message: String?)
}
class MainActivityViewModel {
    var delegate: MainActivityProtocol? = null
    fun getUserDetails(phoneNumber: String, activity: ComponentActivity) {
        var data = JSONObject()
        data.put("phoneNumber",phoneNumber)

        NetWorkCallsManagers.getInstance().postRequest(activity,RequestMethodType.singintapido, data,
            object : NetWorkCallsManagers.CompletionHandler<JSONObject>{
                override fun onSuccess(result: JSONObject) {
                    delegate?.onSuccess(result)
                }

                override fun onError(errorMessage: String) {
                    delegate?.onFailure(errorMessage)
                }
        });
    }
}