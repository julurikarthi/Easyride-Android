package com.easyride.driverapp.UIScreens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.easyride.driverapp.Adapters.CountriesAdapter
import com.easyride.driverapp.LoginSinnupViewModel.Country
import com.easyride.driverapp.LoginSinnupViewModel.SignupViewModel
import com.easyride.driverapp.LoginSinnupViewModel.SignupViewModelInterface
import com.easyride.driverapp.LoginSinnupViewModel.SingupModel
import com.easyride.driverapp.R
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.Utilitys.Preferences
import com.easyride.driverapp.Utilitys.Utilitys
import com.google.android.material.snackbar.Snackbar


class LoginSignUpScreen : ComponentActivity(), SignupViewModelInterface, TextWatcher {
    var viewModel = SignupViewModel()
    var countries: List<Country>? = null
    var countyimageview: TextView? = null
    var countycodeView: TextView? = null
    var firstName: EditText? = null
    var lastName: EditText? = null
    var phoneNumber: EditText? = null
    var progressvie: ProgressBar? = null
    var continebtn: TextView? = null
    var loginbtn: Button? = null
    var signupViewfeilds: LinearLayout? = null
    var phonum = ""
    init {
        viewModel.viewModelinterface = this
    }
    var isloginUSer = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginsignupscreen)
        actionBar?.hide()
        countyimageview = findViewById(R.id.countyimageview)
        countycodeView = findViewById(R.id.countycodeView)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        phoneNumber = findViewById(R.id.phoneNumber)
        progressvie = findViewById(R.id.progressvie)
        continebtn = findViewById(R.id.continebtn)
        loginbtn = findViewById(R.id.loginbtn)
        signupViewfeilds = findViewById(R.id.signupViewfeilds)
        loginbtn?.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        continebtn?.setOnClickListener {
            singupUser()
        }
        firstName?.addTextChangedListener(this)
        lastName?.addTextChangedListener(this)
        phoneNumber?.addTextChangedListener(this)

        viewModel.getAllCountries()


        loginbtn?.setOnClickListener {
            if(signupViewfeilds?.visibility == View.GONE) {
                loginbtn?.text = "Login"
                signupViewfeilds?.visibility = View.VISIBLE
                isloginUSer = false
            } else {
                loginbtn?.text = "SingUp"
                signupViewfeilds?.visibility = View.GONE
                isloginUSer = true
            }
        }

        /// hiding keyboard
        val rootView: View = findViewById(android.R.id.content)
        setupUI(rootView)
    }

    override fun getallcounties(countries: List<Country>?) {
        this.countries = countries
        this.countries?.let {
            showSpinnerDialog(this,it, onItemSelected = {

            })
        }
    }

    private fun setupUI(view: View) {
        // Set up touch listener on root view
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Dismiss the keyboard when a touch event occurs outside of an input field
                hideKeyboard()
            }
            false
        }
    }

    fun singupUser() {
        if (isloginUSer == true) {
            var phoneNumbervalue: String = ""
            this.phoneNumber?.let {
                progressvie?.visibility = View.VISIBLE
                phoneNumbervalue = it.text.toString()
                val dailcode = countycodeView?.text.toString()
                phonum = dailcode + phoneNumbervalue
                viewModel.singinUser(phonum)
            }
            if(phoneNumbervalue.toString().count() != 10){
                this.phoneNumber?.setError("Please Enter phone number")
                return
            }
            return
        }
        var firstNameValue: String = ""
        this.firstName?.let {
            firstNameValue = it.text.toString()
        }
        if(firstNameValue.isEmpty()){
            this.firstName?.setError("Please Enter First Name")
            return
        }
        var lastNamevalue: String = ""
        this.lastName?.let {
            lastNamevalue = it.text.toString()
        }

        if(lastNamevalue.isEmpty()){
            this.lastName?.setError("Please Enter Last Name")
            return
        }
        var phoneNumbervalue: String = ""
        this.phoneNumber?.let {
            phoneNumbervalue = it.text.toString()
        }

        if(phoneNumbervalue.toString().count() != 10){
            this.phoneNumber?.setError("Please Enter phone number")
            return
        }
        progressvie?.visibility = View.VISIBLE
        val dailcode = countycodeView?.text.toString()
        phonum = dailcode + phoneNumbervalue
        var signupModel = SingupModel(firstNameValue, lastNamevalue, phonum, "false")
        viewModel.singinUser(signupModel)
    }

    override fun onSuccesssingup(driverID: String) {
        var preference = MySharedPreferences(this)
        preference.save(Constants.driverId, driverID)
        preference.save(Constants.phonNumber, phonum)
        progressvie?.visibility = View.GONE
        val otpverificationintent = Intent(this, OTPVerifyScreen::class.java)
        startActivity(otpverificationintent)
    }

    override fun onFailuresingup() {
        progressvie?.visibility = View.GONE
    }
    override fun getError(string: String?) {
        progressvie?.visibility = View.GONE
        val message = string ?: ""
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun showSpinnerDialog(context: Context, items: List<Country>, onItemSelected: (String) -> Unit) {
        val spiner = findViewById<Spinner>(R.id.fullScreenSpinner)
        val positionselection = getdailLocation(items)

        // Populate the spinner with items
        val adapter = CountriesAdapter(this, items);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout)
        spiner.adapter = adapter
        spiner.setSelection(positionselection)
        spiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = items[position]
                countycodeView?.text = selectedItem.dial_code
                countyimageview?.text = selectedItem.emoji
                adapter.selectedposition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    fun getdailLocation(items: List<Country>): Int {
        val locale: String = this.getResources().getConfiguration().locale.getCountry()
        for ((index, item) in items.withIndex()) {
            if (locale.equals(item.code)) {
                return index
            }
        }
        return 0
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (isloginUSer == true && phoneNumber?.text?.length == 10) {
            continebtn?.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            return
        } else {
            continebtn?.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }
        if (!firstName?.text.isNullOrBlank() && !lastName?.text.isNullOrBlank() && phoneNumber?.text?.length == 10) {
            continebtn?.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        } else {
            continebtn?.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

    }

    override fun afterTextChanged(s: Editable?) {
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
