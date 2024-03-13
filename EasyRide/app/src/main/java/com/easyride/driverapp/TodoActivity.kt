package com.easyride.driverapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.constraintlayout.widget.ConstraintLayout
import com.easyride.driverapp.UIScreens.AddCarDetailsActivity
import com.easyride.driverapp.UIScreens.CameraActivity
import com.easyride.driverapp.UIScreens.ProfileDetailsActivity
import com.easyride.driverapp.UIScreens.SocialDocument
import com.easyride.driverapp.viewmodels.DocumentExistence
import com.easyride.driverapp.viewmodels.TodoViewModel
import com.easyride.driverapp.viewmodels.TodoViewModelProtocol
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable


interface TodoActivityDelegate: Serializable {
    fun onSuccessAddingCar()
    fun onSuccessAddingDrivingLicense()
    fun onSuccessAddingSocialDocument()
    fun onSuccessAddingProfile()

}
class TodoActivity : ComponentActivity(), TodoActivityDelegate, TodoViewModelProtocol {
    var addCarDetailsView: LinearLayout? = null
    var profileDetailsView: LinearLayout? = null
    var socialdocumenttile: LinearLayout? = null
    var drivinglicenceView: LinearLayout? = null
    var carsuccessicon: ImageView? = null
    var driversuccesicon: ImageView? = null
    var socialidicon: ImageView? = null
    var profilephotosuccuss: ImageView? = null
    lateinit var clmain : ConstraintLayout

    var viewModel = TodoViewModel()
    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val resultKeyData = data?.getStringExtra("ItemAdded")
                when(resultKeyData) {
                    "todocarAdded" -> {
                        onSuccessAddingCar()
                    }
                    "todocsocialDocumentAdded" -> {
                        onSuccessAddingSocialDocument()
                    }
                    "profileDetailsAdded" -> {
                        onSuccessAddingProfile()
                    }
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.todo_activity)
        viewModel.delegate = this
        viewModel.getDocumentSubmitDetails()

        carsuccessicon = findViewById(R.id.carsuccessicon)
        driversuccesicon = findViewById(R.id.driversuccesicon)
        socialidicon = findViewById(R.id.socialidicon)
        drivinglicenceView = findViewById(R.id.drivinglicenceView)
        profilephotosuccuss = findViewById(R.id.profilephotosuccuss)
        actionBar?.hide()
        addCarDetailsView = findViewById(R.id.addCarDetailsView)
        socialdocumenttile = findViewById(R.id.socialdocumenttile)
        profileDetailsView = findViewById(R.id.profileDetailsView)
        addCarDetailsView?.setOnClickListener {
            val addCarDetailsActivity = Intent(this, AddCarDetailsActivity::class.java)
            someActivityResultLauncher.launch(addCarDetailsActivity)
        }
        socialdocumenttile?.setOnClickListener {
            val socialDetailsActivity = Intent(this, SocialDocument::class.java)
            someActivityResultLauncher.launch(socialDetailsActivity)
        }

        profileDetailsView?.setOnClickListener {
            val profileDetailsActivity = Intent(this, ProfileDetailsActivity::class.java)
            someActivityResultLauncher.launch(profileDetailsActivity)
        }
        drivinglicenceView?.setOnClickListener {
            val profileDetailsActivity = Intent(this, CameraActivity::class.java)
            someActivityResultLauncher.launch(profileDetailsActivity)
        }
    }

    override fun onSuccessAddingCar() {
        carsuccessicon?.visibility = View.VISIBLE
    }

    override fun onSuccessAddingDrivingLicense() {
        driversuccesicon?.visibility = View.VISIBLE
    }

    override fun onSuccessAddingSocialDocument() {
        socialidicon?.visibility = View.VISIBLE
    }

    override fun onSuccessAddingProfile() {
        profilephotosuccuss?.visibility = View.VISIBLE

    }

    override fun onSuccess(model: DocumentExistence) {
        if(model.vehicleexist) {
            onSuccessAddingCar()
        }
        if(model.socialdocumentsexist) {
            onSuccessAddingSocialDocument()
        }
        if(model.profiledetailsexist) {
            onSuccessAddingProfile()
        }
        if(model.drivingdetailsexist) {
            onSuccessAddingDrivingLicense()
        }
    }

    override fun onFailure(error: String?) {
    }

}

