package com.easyride.driverapp.Services
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.easyride.driverapp.Adapters.TapidoApplication
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.HomeSceenActivity
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.google.android.gms.location.*


class LocationForegroundService() : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val binder = LocalBinder()


    inner class LocalBinder : Binder() {
        fun getService(): LocationForegroundService {
            // Return this instance of LocationForegroundService so clients can call public methods
            return this@LocationForegroundService
        }
    }


    override fun onCreate() {
        super.onCreate()

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location request
        val pref = MySharedPreferences(TapidoApplication.getAppContext())
        val driverStatus = pref.getBoolean("driverStatus")
        val ontripStatus = pref.getBoolean("ontripStatus")
        if (driverStatus == true && ontripStatus == true) {
            locationRequest = LocationRequest.create().apply {
                interval = 300000 // Update location every 10 seconds
                fastestInterval = 5000 // Fastest update interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        } else if (driverStatus == true) {
            locationRequest = LocationRequest.create().apply {
                interval = 60000 // Update location every 10 seconds
                fastestInterval = 5000 // Fastest update interval
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        }


        // Set up location callback
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                for (location in p0.locations) {
                    // Handle location updates here
                    handleLocation(location)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        createNotificationChannel()

        val notificationIntent = Intent(this, HomeSceenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    fun broadcastData(data: String) {
        val intent = Intent("$packageName.LOCATION_DATA_UPDATED")
        intent.putExtra("liveLocation", data)
        sendBroadcast(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun handleLocation(location: Location) {
        // Handle location updates here
        val latitude = location.latitude
        val longitude = location.longitude
        val data = "Latitude: $latitude, Longitude: $longitude"
        broadcastData(data)
    }

    companion object {
        private const val CHANNEL_ID = "LocationServiceChannel"
    }

}