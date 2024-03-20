package com.easyride.driverapp.viewmodels

import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import org.json.JSONObject

interface  MainActivityProtocol {
    fun onSuccess(result: JSONObject?)
    fun onFailure(message: String?)
}
class MainActivityViewModel {
    var delegate: MainActivityProtocol? = null
    fun getUserDetails(phoneNumber: String) {
        var data = JSONObject()
        data.put("phoneNumber",phoneNumber)

        NetWorkManagers.getInstance().postRequest(RequestMethodType.singintapido, data, object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                delegate?.onSuccess(result)
            }
            override fun onError(errorMessage: String?) {
                delegate?.onFailure(errorMessage)
            }
        })
    }
}