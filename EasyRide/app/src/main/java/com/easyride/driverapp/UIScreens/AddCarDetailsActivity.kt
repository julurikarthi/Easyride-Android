package com.easyride.driverapp.UIScreens

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.easyride.driverapp.Adapters.CustomSpinnerAdapter
import com.easyride.driverapp.R
import com.easyride.driverapp.TodoActivityDelegate
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.Preferences
import com.easyride.driverapp.viewmodels.AddCarDetailsInterface
import com.easyride.driverapp.viewmodels.AddCarModelData
import com.easyride.driverapp.viewmodels.AddcarDetailsViewModel
import com.easyride.driverapp.viewmodels.Car
import com.easyride.driverapp.viewmodels.CarModelsData


public class AddCarDetailsActivity : ComponentActivity(), AddCarDetailsInterface {
    var yearSpinner: Spinner? = null
    var makeSpinner: Spinner? = null
    var modelSpinner: Spinner? = null
    var colorSpinner: Spinner? = null
    var doorsSpinner: Spinner? = null
    var carNumber: EditText? = null
    var savebtn: Button? = null
    var years: MutableList<String> = mutableListOf()
    var colorsList: MutableList<String> = mutableListOf()
    var doorlsList: MutableList<String> = mutableListOf()
    var makeBrands: MutableList<String> = mutableListOf()
    var carsmodels: MutableList<String> = mutableListOf()
    var addcarprogressview: ProgressBar? = null
    private var addtoCarDetailsViewModel = AddcarDetailsViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addcardetails)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        yearSpinner = findViewById(R.id.yearsspinnerframe)
        makeSpinner = findViewById(R.id.makespinner)
        modelSpinner = findViewById(R.id.modelspinner)
        colorSpinner = findViewById(R.id.colorspinner)
        doorsSpinner = findViewById(R.id.doorsspinner)
        carNumber = findViewById(R.id.carNumber)
        savebtn = findViewById(R.id.savebtn)
        addcarprogressview = findViewById(R.id.addcarprogressview)
        actionBar?.title = "Edit Vehicle Info"
        addtoCarDetailsViewModel.delegate = this
        addtoCarDetailsViewModel.getAllcarsDetails()
        val intent = intent
        carNumber?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (yearSpinner?.selectedItemPosition!! > 0 && makeSpinner?.selectedItemPosition!! > 0
                    && modelSpinner?.selectedItemPosition!! > 0 && colorSpinner?.selectedItemPosition!! > 0
                    && doorsSpinner?.selectedItemPosition!! > 0) {
                    savebtn?.setTextColor(Color.WHITE)
                } else {
                    savebtn?.setTextColor(Color.BLACK)
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // This method is called to notify you that somewhere within `s` the characters have been changed.
                val enteredText = s.toString()
                // Do something with the entered text
                println("Entered text: $enteredText")

            }
        })

        savebtn?.setOnClickListener {
            val year = yearSpinner?.selectedItemPosition?.let { it1 -> years.get(it1) }
            val carmake = makeSpinner?.selectedItemPosition?.let { it1 -> makeBrands.get(it1) }
            val carModel = modelSpinner?.selectedItemPosition?.let { it1 -> carsmodels.get(it1) }
            val carColor = colorSpinner?.selectedItemPosition?.let { it1 -> colorsList.get(it1) }
            val cardoors = doorsSpinner?.selectedItemPosition?.let { it1 -> doorlsList.get(it1) }
            val carNumberValue = carNumber?.text.toString()
            if (year?.isNotEmpty() == true && carmake?.isNotEmpty() == true
                && carModel?.isNotEmpty() == true && carColor?.isNotEmpty() == true &&
                cardoors?.isNotEmpty() == true && carNumberValue?.isNotEmpty() == true
            ) {
                val driverId = Preferences.getInstance().getString(Constants.driverId, "")
                var addCarmodel = AddCarModelData(
                    driverId = driverId!!,
                    year = year,
                    make = carmake,
                    model = carModel,
                    color = carColor,
                    doors = cardoors,
                    carNumber = carNumberValue!!
                )
                addcarprogressview?.visibility = View.VISIBLE
                addtoCarDetailsViewModel.addCarDetails(addCarmodel)
            }

        }
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


    override fun getCarDetailsModel(cardetailsModel: CarModelsData?) {
        cardetailsModel?.years?.let { yearsList ->
            years = yearsList.toMutableList()
            years.add(0, "Select Year")
            val adapter = CustomSpinnerAdapter(this, R.layout.custom_spinner_item, years)
            yearSpinner?.adapter = adapter
        }

        cardetailsModel?.carcolors?.let { carsList ->
            colorsList = carsList.toMutableList()
            colorsList.add(0, "Select Color")
            val adapter = CustomSpinnerAdapter(this, R.layout.custom_spinner_item, colorsList)
            colorSpinner?.adapter = adapter
        }

        cardetailsModel?.doors?.let { carsList ->
            doorlsList = carsList.toMutableList()
            doorlsList.add(0, "Select Number of Doors")
            val adapter = CustomSpinnerAdapter(this, R.layout.custom_spinner_item, doorlsList)
            doorsSpinner?.adapter = adapter
        }

        cardetailsModel?.let {
            makeBrands = filterbrands(it.cars)
            makeBrands.add(0, "Select Car Brand")
            val adapter = CustomSpinnerAdapter(this, R.layout.custom_spinner_item, makeBrands)
            makeSpinner?.adapter = adapter
            makeSpinner?.setSelection(0)
        }

        makeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Get the selected item
                if (position != 0) {

                    cardetailsModel?.cars?.let {
                        carsmodels =  it.get(position - 1).models
                        val adapter = CustomSpinnerAdapter(this@AddCarDetailsActivity, R.layout.custom_spinner_item, carsmodels)
                        modelSpinner?.adapter = adapter
                    }
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Handle the case where nothing is selected (optional)
            }
        }


    }

    override fun getError(error: String?) {

    }

    override fun getAddCarError(error: String?) {
        Toast.makeText(this, error!!, Toast.LENGTH_SHORT).show()
    }

    override fun OnSuccessAddCar() {
        addcarprogressview?.visibility = View.GONE
        val resultIntent = Intent()
        resultIntent.putExtra("ItemAdded", "todocarAdded")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun filterbrands(listofcars: List<Car>) : MutableList<String> {
        var  brands: MutableList<String> = mutableListOf()
        for (brand in listofcars) {
            brands.add(brand.brand)
        }
        return brands
    }
}