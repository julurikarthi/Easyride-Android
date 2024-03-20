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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.easyride.driverapp.R
import com.easyride.driverapp.Services.LocationForegroundService
import com.easyride.driverapp.UIScreens.ui.home.HomeFragment
import com.easyride.driverapp.UIScreens.ui.home.HomeFragmentInterface
import com.google.android.material.navigation.NavigationView
class HomeSceenActivity() : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HomeFragmentInterface {

    private var drawerLayout: DrawerLayout? = null
    var menuicon: ImageView? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    var homefragment: HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.easyride.driverapp.R.layout.homeview)
        menuicon = findViewById(R.id.menuicon)
        drawerLayout = findViewById<DrawerLayout>(com.easyride.driverapp.R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(com.easyride.driverapp.R.id.nav_view)
        navigationView.visibility = View.VISIBLE
        navigationView.setNavigationItemSelectedListener(this)
        homefragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homefragment!!)
            .commit()
        navigationView.setCheckedItem(R.id.nav_home)
        menuicon?.setOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
        requestforLocationPermission()
//        startLocationService()
    }

    fun startLocationService() {
        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        startService(serviceIntent)
    }

    fun requestforLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
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
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.nav_home -> supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, homefragmenewt).commit()
//
//            R.id.nav_logout -> Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
//        }
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

// Extract latitude and longitude from the split parts
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

// Check if latitude and longitude are not null
        if (latitude != null && longitude != null) {
            // Do something with latitude and longitude
            println("Latitude: $latitude, Longitude: $longitude")
//            homefragment?.setLocation(latitude = latitude, longitude = longitude)
        } else {
            println("Failed to parse latitude or longitude from data.")
        }
    }

    }

