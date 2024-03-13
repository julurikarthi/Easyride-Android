package com.easyride.driverapp.UIScreens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
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
import androidx.core.content.ContextCompat
import com.easyride.driverapp.R
import com.easyride.driverapp.TodoActivity
import com.easyride.driverapp.UIScreens.ui.theme.EasyRideTheme

class OTPVerifyScreen : ComponentActivity(), TextWatcher {
    var otpnext: TextView?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otpverifyscreen)
        otpnext = findViewById(R.id.otpnext)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        actionBar?.title = ""
        otpnext?.setOnClickListener {
            val homeactivityintent = Intent(this, TodoActivity::class.java)
            startActivity(homeactivityintent)
        }
        otpnext?.addTextChangedListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (otpnext?.text?.count() == 4) {
            otpnext?.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            otpnext?.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

}

