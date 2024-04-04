package com.easyride.driverapp.UIScreens

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.widget.addTextChangedListener
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.ui.theme.EasyRideTheme
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.Utilitys.Preferences
import com.easyride.driverapp.viewmodels.SocialDocumentsViewModel
import com.easyride.driverapp.viewmodels.SocialDocumentsViewModelProtocol

class SocialDocument : ComponentActivity(), TextWatcher, SocialDocumentsViewModelProtocol {
    var savesocialbtn: Button? = null
    var socialNumber: EditText? = null
    var reconfirmsocialNumber: EditText? = null
    var isSocialNumberEnter = false
    var isreconfirmSocialNumberEnter = false
    var viewModel = SocialDocumentsViewModel()
    var addcarprogressviewSocialView: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.socialdocument)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        savesocialbtn = findViewById(R.id.savesocialbtn)
        socialNumber = findViewById(R.id.socialNumber)
        addcarprogressviewSocialView = findViewById(R.id.addcarprogressviewSocialView)
        reconfirmsocialNumber = findViewById(R.id.reconfirmsocialNumber)
        socialNumber?.addTextChangedListener(this)
        reconfirmsocialNumber?.addTextChangedListener(this)
        viewModel.delegate = this

        savesocialbtn?.setOnClickListener {
            if (isSocialNumberEnter && isreconfirmSocialNumberEnter) {
                var preference = MySharedPreferences(this)
                var dirverid = preference.getName(Constants.driverId)
                var socialNumber = socialNumber?.text.toString()
                addcarprogressviewSocialView?.visibility = View.VISIBLE
                viewModel.submitSocialDocument(dirverid!!, socialNumber)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(socialNumber?.text.toString().equals(reconfirmsocialNumber?.text.toString())) {
                    savesocialbtn?.setTextColor(Color.WHITE)
                } else {
                    savesocialbtn?.setTextColor(Color.BLACK)
                }
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == socialNumber?.getEditableText()) {
            isSocialNumberEnter = true
        } else if (s == reconfirmsocialNumber?.getEditableText()) {
            isreconfirmSocialNumberEnter = true
        }
        if (socialNumber?.text.toString().isEmpty()) {
            isSocialNumberEnter = false
        }
        if (reconfirmsocialNumber?.text.toString().isEmpty()) {
            isreconfirmSocialNumberEnter = false
        }
    }

    override fun onSuccess() {
        addcarprogressviewSocialView?.visibility = View.GONE
        val resultIntent = Intent()
        resultIntent.putExtra("ItemAdded", "todocsocialDocumentAdded")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onFailure(error: String?) {
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
}
