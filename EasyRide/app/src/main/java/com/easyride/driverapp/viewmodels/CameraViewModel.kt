package com.easyride.driverapp.viewmodels

import android.graphics.Bitmap
import android.util.Log
import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.easyride.driverapp.Utilitys.Preferences
import com.easyride.driverapp.Utilitys.Utilitys
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

interface CameraViewModelProtocol {
    fun onsuccessImageUpload(imageid: String?)
    fun onsavesuccessdrivinglicense()
}
class CameraViewModel {
    var delegate: CameraViewModelProtocol? = null

    fun uploadImage(bitMap: Bitmap) {
        val drivingId = Preferences.driverId
        var utility = Utilitys()
        var file = utility.saveBitmapToFile(bitMap,"captured_image.jpg")
        file.let {
            NetWorkManagers.getInstance().uploadImage(it, object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?
                ) {
                    Log.e("updloadImageStatus", "" + statusCode)
                    val jsonObject = responseBody?.toString(Charsets.UTF_8)
                   val imageid =  parseJson(jsonObject.toString())?.imageId
                    delegate?.onsuccessImageUpload(imageid)
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable?
                ) {
                    Log.e("failing updloadImageStatus", "" + statusCode)
                }
            })
        }

    }

    fun byteArrayToJson(byteArray: ByteArray?): JsonObject? {
        if (byteArray == null) return null

        val jsonString = byteArray.toString(Charsets.UTF_8) // Convert ByteArray to String
        return Gson().fromJson(jsonString, JsonObject::class.java) // Parse JSON string to JSON object
    }

    fun parseJson(jsonString: String): ImageData? {
        return try {
            Gson().fromJson(jsonString, ImageData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveDrivingLicense(dirverid: String, imageId: String) {
        var jsonObject = JSONObject()
        jsonObject.put("driverId", dirverid)
        jsonObject.put("licenseImage", imageId)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.addDrivingLicense, jsonObject,
            object : CompletionHandler<JSONObject> {
                override fun onSuccess(result: JSONObject?) {
                delegate?.onsavesuccessdrivinglicense()
                }

                override fun onError(errorMessage: String?) {

                }
            })
    }
}

data class ImageData(val imageId: String)
