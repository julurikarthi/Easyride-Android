package com.easyride.driverapp.viewmodels

import android.util.Log
import com.easyride.driverapp.NetWorkManager.NetWorkManagers
import com.easyride.driverapp.NetWorkManager.NetWorkManagers.CompletionHandler
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.easyride.driverapp.NetWorkManager.URLParams
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

interface AddCarDetailsInterface {
    fun getCarDetailsModel(cardetailsModel: CarModelsData?)
    fun getError(error: String?)
    fun getAddCarError(error: String?)
    fun OnSuccessAddCar()
}
class AddcarDetailsViewModel {

    var delegate: AddCarDetailsInterface? = null

    fun getAllcarsDetails() {
        NetWorkManagers.getInstance().getRequest(URLParams.allcarsApis, object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                var carModel = convertocardetailsModel(jsonObject = result)
                delegate?.getCarDetailsModel(carModel)
            }
            override fun onError(errorMessage: String?) {
                delegate?.getError(errorMessage)
            }
        })
    }

   private fun convertocardetailsModel(jsonObject: JSONObject?): CarModelsData? {
        jsonObject?.let {
            val gson = Gson()
            val countryListType = object : TypeToken<CarModelsData>() {}.type
            val carModel: CarModelsData = gson.fromJson(jsonObject.toString(), countryListType)
            return  carModel
        } ?: run {
            Log.e("convertoModel", "Received a null JSONObject")
        }
        return null
    }

    fun addCarDetails(model: AddCarModelData) {
        val gson = Gson()
        val jsonString = gson.toJson(model)
        val jsonObject = JSONObject(jsonString)
        NetWorkManagers.getInstance().postRequest(RequestMethodType.addVehicleDetails, jsonObject, object : CompletionHandler<JSONObject> {
            override fun onSuccess(result: JSONObject?) {
                delegate?.OnSuccessAddCar()
            }
            override fun onError(errorMessage: String?) {
                delegate?.getAddCarError(errorMessage)
            }
        })
    }
}

data class CarModelsData (
    val cars: List<Car>,
    val carcolors: List<String>,
    val years: List<String>,
    val doors: List<String>
)

data class Car (
    val brand: String,
    val models: MutableList<String>
)

data class AddCarModelData(
    val driverId: String,
    val year: String,
    val make: String,
    val model: String,
    val color: String,
    val doors: String,
    val carNumber: String
)
