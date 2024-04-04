package com.easyride.driverapp.UIScreens

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.easyride.driverapp.MainActivity
import com.easyride.driverapp.R

class OTPVerifyScreen : ComponentActivity(), TextWatcher {
    var otpnext: TextView?= null
    var otpedittext: EditText?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otpverifyscreen)
        otpnext = findViewById(R.id.otpnext)
        otpedittext = findViewById(R.id.otpedittext)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        actionBar?.title = ""
        otpnext?.setOnClickListener {
            val homeactivityintent = Intent(this, MainActivity::class.java)
            startActivity(homeactivityintent)
        }
        otpedittext?.addTextChangedListener(this)
        val maxLength = 4
        otpedittext?.filters = arrayOf(InputFilter.LengthFilter(maxLength))
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
        if (otpedittext?.text?.count() == 4) {
            otpnext?.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            otpnext?.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        }
    }

    override fun afterTextChanged(s: Editable?) {
    }

}

