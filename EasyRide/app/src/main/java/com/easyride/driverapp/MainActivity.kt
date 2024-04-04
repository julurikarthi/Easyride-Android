package com.easyride.driverapp
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.easyride.driverapp.Adapters.ApplicationContextProvider
import com.easyride.driverapp.UIScreens.HomeSceenActivity
import com.easyride.driverapp.UIScreens.LoginSignUpScreen
import com.easyride.driverapp.UIScreens.TodoActivity
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.viewmodels.MainActivityProtocol
import com.easyride.driverapp.viewmodels.MainActivityViewModel
import com.google.firebase.installations.FirebaseInstallations
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : ComponentActivity(), MainActivityProtocol {

    private var viewModel = MainActivityViewModel()
    var pref: MySharedPreferences? = null
    val REQUEST_LOCATION_PERMISSION_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        pref = MySharedPreferences(this)
        actionBar?.hide()
        ApplicationContextProvider.setApplicationContext(this)
        viewModel.delegate = this

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION_CODE
            )
        } else {
            processdashboard()
        }


    }

    fun processdashboard() {
        var driverId = pref?.getName(Constants.driverId) ?: ""
        if (driverId.isEmpty() == true) {
            val intent = Intent(this, LoginSignUpScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            var phhonenumber = pref?.getName(Constants.phonNumber) ?: ""
            viewModel.getUserDetails(phhonenumber,this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               processdashboard()
            }
        }
    }

    override fun onSuccess(result: JSONObject?) {
        val gson = Gson()
        val driver: Driver = gson.fromJson(result.toString(), Driver::class.java)
        pref?.saveDriverInfo(driver)
        if (driver.isRegistered) {
            val intent = Intent(this, HomeSceenActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, TodoActivity::class.java)
            intent.putExtra("verificationStatus", driver.verificationStatus)
            startActivity(intent)
        }
        finish()

    }

    override fun onFailure(message: String?) {

    }
}

data class Driver(
    val isRegistered: Boolean,
    val driverID: String,
    val verificationStatus: String,
    val firstName: String,
    val lastName: String,
    val registrationDate: String,
    val phoneNumber: String
)