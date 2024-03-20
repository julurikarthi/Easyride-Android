package com.easyride.driverapp
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.easyride.driverapp.Adapters.ApplicationContextProvider
import com.easyride.driverapp.UIScreens.HomeSceenActivity
import com.easyride.driverapp.UIScreens.LoginSignUpScreen
import com.easyride.driverapp.UIScreens.TodoActivity
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.Utilitys.Preferences
import com.easyride.driverapp.viewmodels.MainActivityProtocol
import com.easyride.driverapp.viewmodels.MainActivityViewModel
import org.json.JSONObject

class MainActivity : ComponentActivity(), MainActivityProtocol {

    private var viewModel = MainActivityViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)
        actionBar?.hide()
        ApplicationContextProvider.setApplicationContext(this)
        viewModel.delegate = this

        if (Preferences.driverId?.isEmpty() == true) {
            val intent = Intent(this, LoginSignUpScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            var pref = MySharedPreferences(this)
            var phhonenumber = pref.getName(Constants.phonNumber) ?: ""
            viewModel.getUserDetails(phhonenumber)
        }

    }

    override fun onSuccess(result: JSONObject?) {
        var verificationStatus = result?.getString("verificationStatus")
        if (verificationStatus.equals("true")) {
            val intent = Intent(this, HomeSceenActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, TodoActivity::class.java)
            intent.putExtra("verificationStatus", verificationStatus)
            startActivity(intent)
        }
        finish()

    }

    override fun onFailure(message: String?) {

    }
}
