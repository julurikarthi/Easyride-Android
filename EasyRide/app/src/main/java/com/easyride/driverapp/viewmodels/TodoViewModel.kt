package com.easyride.driverapp.viewmodels

import android.util.Log
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
interface TodoViewModelProtocol {
    fun onSuccess(model: DocumentExistence)
    fun onFailure(error: String?)
}
class TodoViewModel {

    var delegate: TodoViewModelProtocol? = null

    fun getDocumentSubmitDetails(dirverid: String) {
        var jsonObje = JSONObject()
        jsonObje.put("driverId",dirverid)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.userDoumentpending,
            jsonObje,
            object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                val model = convertoModel(result)
                model?.let { delegate?.onSuccess(it) }
            }
            override fun onError(errorMessage: String?) {
                delegate?.onFailure(errorMessage)
            }
        })
    }

    private fun convertoModel(jsonObject: JSONObject?): DocumentExistence? {
        jsonObject?.let {
            val gson = Gson()
            val countryListType = object : TypeToken<DocumentExistence>() {}.type
            val carModel: DocumentExistence = gson.fromJson(jsonObject.toString(), countryListType)
            return  carModel
        } ?: run {
            Log.e("convertoModel", "Received a null JSONObject")
        }
        return null
    }
 }

data class DocumentExistence(
    val vehicleexist: Boolean,
    val socialdocumentsexist: Boolean,
    val profiledetailsexist: Boolean,
    val drivingdetailsexist: Boolean
)