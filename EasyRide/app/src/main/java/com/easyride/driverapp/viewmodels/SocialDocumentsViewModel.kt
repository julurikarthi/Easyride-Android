package com.easyride.driverapp.viewmodels

import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.google.gson.JsonObject
import org.json.JSONObject

interface SocialDocumentsViewModelProtocol {
    fun onSuccess()
    fun onFailure(error: String?)
}

class SocialDocumentsViewModel {

    var delegate: SocialDocumentsViewModelProtocol? = null

    fun submitSocialDocument(drivingId: String, socialDocumer: String) {
        var jsondata = JSONObject()
        jsondata.put("driverId", drivingId)
        jsondata.put("socialNumber", socialDocumer)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.addSocialDocument,
            jsondata,
            object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                delegate?.onSuccess()
            }
            override fun onError(errorMessage: String?) {
                delegate?.onFailure(errorMessage)
            }
        })
    }
}