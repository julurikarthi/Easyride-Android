package com.easyride.driverapp.LoginSinnupViewModel

import android.text.Editable
import android.util.Log
import com.easyride.driverapp.NetWorkManager.NetWorkCallsManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.easyride.driverapp.NetWorkManager.URLParams
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


interface  SignupViewModelInterface {
    fun  getallcounties(countries: List<Country>?)
    fun getError(string: String?)
    fun onSuccesssingup(driverID: String)
    fun onFailuresingup()
}
class SignupViewModel {

    var viewModelinterface: SignupViewModelInterface? = null

    fun getAllCountries() {
        NetWorkManagers.getInstance().getRequest(URLParams.countriesApi, object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                convertoModel(result)
            }
            override fun onError(errorMessage: String?) {
                viewModelinterface?.getError(errorMessage)
            }
        })
        }

    fun convertoModel(jsonObject: JSONObject?) {
        jsonObject?.let {
            val array: JSONArray = it.getJSONArray("countries")
            val gson = Gson()
            val countryListType = object : TypeToken<List<Country>>() {}.type
            val countries: List<Country> = gson.fromJson(array.toString(), countryListType)
            Log.i("countries count", countries.count().toString())
            viewModelinterface?.getallcounties(countries)
        } ?: run {
            Log.e("convertoModel", "Received a null JSONObject")
        }
    }

    fun singinUser(singupModel: SingupModel) {
        val gson = Gson()
        val jsonString = gson.toJson(singupModel)
        val jsonObject = JSONObject(jsonString)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.registertapido,
                                                    jsonObject,
                                                    object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                result?.let {
                    val driverId = it.get("driverId").toString()
                    viewModelinterface?.onSuccesssingup(driverID = driverId)
                }
                Log.e("result", result.toString())
            }
            override fun onError(errorMessage: String?) {
                Log.e("Error view", errorMessage.toString())
                viewModelinterface?.onFailuresingup()
                viewModelinterface?.getError(errorMessage)
            }
        })
    }

    fun singinUser(phoneNumber: String) {
        val jsonObject = JSONObject()
        jsonObject.put("phoneNumber", phoneNumber)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.singintapido, jsonObject,
            object : CompletionHandler<JSONObject> {
                override fun onSuccess(result: JSONObject?) {
                    result?.let {
                        val driverID = result.getString("driverID")
                        val isRegistered = result.getBoolean("isRegistered")
                        if (isRegistered) {
                            viewModelinterface?.onSuccesssingup(driverID = driverID)
                        }
                    }
                }

                override fun onError(errorMessage: String?) {
                    viewModelinterface?.getError(errorMessage)
                }
            })

    }

}

data class SingupModel(val firstName: String, val lastName: String, val phoneNumber: String, val verficationStatus: String)