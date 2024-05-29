package com.easyride.driverapp.viewmodels

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.easyride.driverapp.NetWorkManager.NetWorkCallsManagers
import com.easyride.driverapp.NetWorkManager.RequestMethodType
import com.easyride.driverapp.UIScreens.ui.home.HomeFragmentInterface
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import org.json.JSONObject
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HomeFragmentViewModel {
    var delegate: HomeFragmentInterface? = null
    val REQUEST_LOCATION_PERMISSION_CODE = 1001
    private val handler = Handler(Looper.getMainLooper())

    fun getDirectionsUrl(origin: com.google.android.gms.maps.model.LatLng, dest: com.google.android.gms.maps.model.LatLng): String? {

        // Origin of route
        val str_origin =
            "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest =
            "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val key = "key=AIzaSyCIVEtSk-0GZBgeVVezSYSnKMmuxb6umSM"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode&$key"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=AIzaSyCIVEtSk-0GZBgeVVezSYSnKMmuxb6umSM"
    }

    suspend fun gotoOnline(activity: Activity, fusedLocationClient: FusedLocationProviderClient, driverStatus: Boolean, ontripStatus: Boolean) {

        val location = getLocation(activity, fusedLocationClient)
        val preferences = MySharedPreferences(activity)
        var driverId = preferences.getName(Constants.driverId) ?: ""
        if(driverId.isNotEmpty()) {
            val driverActiveLocation = ""+location?.latitude + "," + ""+location?.longitude ?: ""
            val city = location?.city ?: ""
            val state = location?.state ?: ""
            handler.post {
                updateDriverStatus(activity, driverId = driverId, driverActiveLocation = driverActiveLocation, driverActiveZipcode = location?.zipCode ?: "", driverStatus = driverStatus.toString(), ontripStatus = ontripStatus.toString(), driverCity = city, driverState = state )
            }
        }

    }

    fun updateDriverStatus(activity: Activity, driverId: String, driverActiveLocation: String, driverActiveZipcode: String, driverStatus: String, ontripStatus: String, driverCity: String, driverState: String) {
        var jsonObject = JSONObject()
        jsonObject.put("driverID",driverId)
        jsonObject.put("driverActiveLocation",driverActiveLocation)
        jsonObject.put("driverActiveZipcode",driverActiveZipcode)
        jsonObject.put("driverStatus",driverStatus.toString())
        jsonObject.put("ontripStatus",ontripStatus.toString())
        jsonObject.put("driverCity",driverCity)
        jsonObject.put("driverState",driverState)
        NetWorkCallsManagers.getInstance().postRequest(activity, RequestMethodType.updateDriverTable,
            jsonObject, object : NetWorkCallsManagers.CompletionHandler<JSONObject> {
                override fun onSuccess(result: JSONObject) {
                    delegate?.onSuccesOnlineStatus()
                }
                override fun onError(errorMessage: String) {
                    delegate?.onFailreOnlineStatus(errorMessage)
                }
            })
    }

    suspend fun getLocation(activity: Activity, fusedLocationClient: FusedLocationProviderClient): LocationInfo? {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION_CODE
            )

            // You might return null here or handle the lack of permissions in a different way
            return null
        }
        return suspendCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val geocoder = Geocoder(activity, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        val zipCode = addresses?.get(0)?.postalCode
                        val city = addresses?.get(0)?.locality
                        val state = addresses?.get(0)?.adminArea
                        val locationInfo = LocationInfo(latitude, longitude, zipCode, city, state)
                        continuation.resume(locationInfo)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

}

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val zipCode: String?,
    val city: String?,
    val state: String?
)
