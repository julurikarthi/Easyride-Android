package com.easyride.driverapp.UIScreens

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.ui.theme.EasyRideTheme
import java.util.Objects

class GetStartedScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.getstartedscreen)
        actionBar?.hide()
        var genstartedbtn = findViewById<TextView>(R.id.genstartedbtn)
        genstartedbtn.setOnClickListener {
            val intent = Intent(this, LoginSignUpScreen::class.java)
            startActivity(intent)
        }
    }
}

