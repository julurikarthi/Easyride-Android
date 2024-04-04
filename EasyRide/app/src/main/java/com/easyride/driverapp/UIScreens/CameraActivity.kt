package com.easyride.driverapp.UIScreens


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.Fragments.MyBottomSheetFragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.viewmodels.CameraViewModel
import com.easyride.driverapp.viewmodels.CameraViewModelProtocol

class CameraActivity : AppCompatActivity(), CameraViewModelProtocol {
    var drivingImage: ImageView? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private val CAMERA_PERMISSION_CODE = 101
    var retake: Button? = null
    var save: Button? = null
    var drivingphoto: Bitmap? = null
    var viewModel = CameraViewModel()
    var driverImageId: String? = null
    var progressbarview: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cameralayout)
        actionBar?.title = "Upload Driving license"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        drivingImage = findViewById(R.id.drivingImage)
        retake = findViewById(R.id.retake)
        save = findViewById(R.id.save)
        progressbarview = findViewById(R.id.progressbarview)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewModel.delegate = this
        supportActionBar?.title = "Upload Driving license"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (checkCameraPermission()) {
            takepicture()
        } else {
            requestCameraPermission()
        }
        retake?.setOnClickListener {
            takepicture()
        }
        save?.setOnClickListener {
            if(driverImageId?.isNotEmpty() == true) {
                progressbarview?.visibility = View.VISIBLE
                var preference = MySharedPreferences(this)
                var dirverid = preference.getName(Constants.driverId)
                viewModel.saveDrivingLicense(dirverid!!, driverImageId!!)
            }
        }
    }
    fun takepicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        someActivityResultLauncher.launch(cameraIntent)
    }
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private val someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val photo: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                this.drivingphoto = photo
                drivingImage?.setImageBitmap(photo)
                viewModel.uploadImage(photo!!)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takepicture()
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message)
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

    override fun onsuccessImageUpload(imageid: String?) {
        driverImageId = imageid
    }

    override fun onsavesuccessdrivinglicense() {
        progressbarview?.visibility = View.GONE
        val resultIntent = Intent()
        resultIntent.putExtra("ItemAdded", "todocdrivinglicence")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
