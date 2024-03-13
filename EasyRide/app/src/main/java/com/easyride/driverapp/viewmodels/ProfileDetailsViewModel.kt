package com.easyride.driverapp.viewmodels

import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import org.json.JSONObject

interface  ProfileDetailsDelegate {
   fun onSuccess()
   fun onFailure(errorMessage: String?)
}
class ProfileDetailsViewModel {

    var delegate: ProfileDetailsDelegate? = null

    fun saveProfileDetails(profilePhoto: String, fullAdress: String) {
        var params = JSONObject()
        params.put("fullAddress",fullAdress)
        params.put("profilePhoto",profilePhoto)
        params.put("driverId",com.easyride.driverapp.Utilitys.Preferences.driverId)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.addProfilePhoto,
            params,
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