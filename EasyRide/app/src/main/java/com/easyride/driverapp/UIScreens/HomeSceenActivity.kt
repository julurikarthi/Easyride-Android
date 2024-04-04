package com.easyride.driverapp.UIScreens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.easyride.driverapp.R
import com.easyride.driverapp.ScheduleService.SchedulerService
import com.easyride.driverapp.Services.LocationForegroundService
import com.easyride.driverapp.UIScreens.ui.home.HomeFragment
import com.easyride.driverapp.UIScreens.ui.home.HomeFragmentInterface
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.bottomsheet.BottomDialogFragment
import com.easyride.driverapp.bottomsheet.BottomDialogFragmentdelegate
import com.easyride.driverapp.viewmodels.HomeFragmentViewModel
import com.easyride.driverapp.viewmodels.HomeScreenViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeSceenActivity() : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    HomeFragmentInterface, BottomDialogFragmentdelegate {
    private val homescreenviewModel = HomeScreenViewModel()
    private var drawerLayout: DrawerLayout? = null
    var menuicon: ImageView? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    var homefragment: HomeFragment? = null
    var bottomDialog: BottomDialogFragment? = null
    private var viewmodel = HomeFragmentViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var menuName: TextView? = null
    private var menuPhoneNumber: TextView? = null
    private var pref: MySharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.easyride.driverapp.R.layout.homeview)
        menuicon = findViewById(R.id.menuicon)
        requestforNotificationPermission()
        pref = MySharedPreferences(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        drawerLayout = findViewById<DrawerLayout>(com.easyride.driverapp.R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(com.easyride.driverapp.R.id.nav_view)
        navigationView.visibility = View.VISIBLE
        navigationView.setNavigationItemSelectedListener(this)
        homefragment = HomeFragment()
        homefragment?.delegate = this
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homefragment!!)
            .commit()
        navigationView.setCheckedItem(R.id.nav_home)
        val headerView = navigationView.getHeaderView(0)
        menuName = headerView.findViewById(R.id.menuName)
        menuPhoneNumber = headerView.findViewById(R.id.menuContact)
        menuicon?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
//        startLocationService()

        setProfileDetails()
        registerforService()
        registerforServiceEveryeighthours()

        if (pref?.getBoolean("registeredToken") == false) {
            requestFirebaseToken()
        }

    }

    override fun onResume() {
        super.onResume()
        setLocation()
    }

    private fun setProfileDetails() {
        val name = pref?.getDriverInfo()?.firstName  + pref?.getDriverInfo()?.lastName
        val phoneNumber = pref?.getDriverInfo()?.phoneNumber
        menuName?.text = name
        menuPhoneNumber?.text = phoneNumber
    }

    fun setLocation() {
        CoroutineScope(Dispatchers.IO).launch {
            val location = homefragment?.viewmodel?.getLocation(this@HomeSceenActivity,fusedLocationClient)
            runOnUiThread {
                location?.let { homefragment?.addLocationOnMap(it.latitude, location.longitude) }
            }
        }

    }

    fun createBottonSheet() {
        bottomDialog = BottomDialogFragment()
        bottomDialog?.delegate = this
        bottomDialog?.show(supportFragmentManager, "bottom_dialog")
    }

    fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        startService(serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location access
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission need to find the riders", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            return
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home ->
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()

            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout?.closeDrawer(GravityCompat.START);
        return true;
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onmenubarclicked() {
        drawerLayout?.openDrawer(GravityCompat.START)
    }

    override fun openButtonSheet() {

    }

    override fun onSuccesOnlineStatus() {
        bottomDialog?.dismiss()
    }

    override fun onFailreOnlineStatus(error: String) {
        Toast.makeText(this,error, Toast.LENGTH_LONG).show()
    }

    private val locationDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "$packageName.LOCATION_DATA_UPDATED") {
                val data = intent.getStringExtra("liveLocation")
                Log.e("liveLocation", data.toString())
                parseLocation(data.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction("$packageName.LOCATION_DATA_UPDATED")
        }
        registerReceiver(locationDataReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(locationDataReceiver)
    }

    fun parseLocation(data: String) {
        val parts = data.split(",")

        var latitude: Double? = null
        var longitude: Double? = null

        for (part in parts) {
            val trimmedPart = part.trim()
            if (trimmedPart.startsWith("Latitude:")) {
                latitude = trimmedPart.substringAfter("Latitude:").trim().toDoubleOrNull()
            } else if (trimmedPart.startsWith("Longitude:")) {
                longitude = trimmedPart.substringAfter("Longitude:").trim().toDoubleOrNull()
            }
        }

        if (latitude != null && longitude != null) {
            println("Latitude: $latitude, Longitude: $longitude")
        } else {
            println("Failed to parse latitude or longitude from data.")
        }
    }

    override fun onCloseBtn() {
        bottomDialog?.dismiss()
    }

    fun registerforService() {
        val driverStatus = pref?.getBoolean("driverStatus")
        if(driverStatus == false) {
            createBottonSheet()
        }
    }

    fun registerforServiceEveryeighthours() {
        val service = SchedulerService()
        service.schedule(this)
    }

    override fun onTapOnlinebtn() {
        pref?.saveBoolean("driverStatus", true)
        CoroutineScope(Dispatchers.IO).launch {
            homefragment?.viewmodel?.delegate = this@HomeSceenActivity
            homefragment?.viewmodel?.gotoOnline(
                activity = this@HomeSceenActivity,
                fusedLocationClient = fusedLocationClient,
                driverStatus = true,
                ontripStatus = false
            )
        }
    }

    fun requestforNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestFirebaseToken() {
        GlobalScope.launch {
            FirebaseInstallations.getInstance().getToken(/* forceRefresh = */ true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        // Handle token
                        if (token != null) {
                            homescreenviewModel.registeredforNotification(token)
                        }
                    } else {
                        // Handle error
                        val exception = task.exception
                        exception?.printStackTrace()
                    }
                }
        }

    }

}

