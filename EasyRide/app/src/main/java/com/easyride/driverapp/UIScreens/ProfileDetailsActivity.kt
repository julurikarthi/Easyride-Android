package com.easyride.driverapp.UIScreens

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.Fragments.MyBottomSheetFragment
import com.easyride.driverapp.UIScreens.Fragments.MyBottomSheetFragmentProtocol
import com.easyride.driverapp.Utilitys.Constants
import com.easyride.driverapp.Utilitys.MySharedPreferences
import com.easyride.driverapp.Utilitys.Utilitys
import com.easyride.driverapp.viewmodels.CameraViewModel
import com.easyride.driverapp.viewmodels.CameraViewModelProtocol
import com.easyride.driverapp.viewmodels.ProfileDetailsDelegate
import com.easyride.driverapp.viewmodels.ProfileDetailsViewModel
import com.google.android.material.snackbar.Snackbar


class ProfileDetailsActivity : AppCompatActivity(), ProfileDetailsDelegate,
    MyBottomSheetFragmentProtocol, CameraViewModelProtocol {
    var profileImage: ImageView? = null
    var fulladress: EditText? = null
    var saveprofilebtn: Button? = null
    var viewModel = ProfileDetailsViewModel()
    var progressbarview: ProgressBar? = null
    val bottomSheetFragment = MyBottomSheetFragment()
    val REQUEST_IMAGE_CAPTURE = 1
    var cameraviewModel = CameraViewModel()
    var profileimageid: String? = null
    private val CAMERA_PERMISSION_CODE = 101

    private val profileActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                profileImage?.setImageURI(selectedImageUri)
                val drawable: Drawable? = profileImage?.getDrawable()

                if (drawable is BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    val roundedBitmap = getRoundedCornerBitmap(bitmap)
                    profileImage?.setImageBitmap(roundedBitmap)
                    cameraviewModel.uploadImage(bitmap)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profiledetailsactivity)
        profileImage = findViewById(R.id.profileImage)
        fulladress = findViewById(R.id.fulladress)
        saveprofilebtn = findViewById(R.id.saveprofilebtn)
        progressbarview = findViewById(R.id.progressbarview)
        viewModel.delegate = this
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Profile Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bottomSheetFragment.delegate = this
        cameraviewModel.delegate = this
        val rootView: View = findViewById(android.R.id.content)
        Utilitys.setupUI(rootView, this)
        saveprofilebtn?.setOnClickListener {
            Utilitys.hideKeyboard(this)
            val rootView: View = findViewById(android.R.id.content)

            var address = fulladress?.text.toString()

            if(profileimageid == null) {
                progressbarview?.visibility = View.GONE
                Utilitys.showSnackbar(rootView, "Please Upload Image")
                return@setOnClickListener
            }
            if (address.isEmpty()) {
                progressbarview?.visibility = View.GONE
                Utilitys.showSnackbar(rootView, "Please Enter Full Address")
                return@setOnClickListener
            }
            if (profileimageid.toString().isNotEmpty() && address.isNotEmpty()) {
                progressbarview?.visibility = View.VISIBLE
                profileimageid?.let { it1 ->
                    var preference = MySharedPreferences(this)
                    var dirverid = preference.getName(Constants.driverId)
                    viewModel.saveProfileDetails(dirverid!!,it1,address)
                }
            }
        }
        profileImage?.setOnClickListener {
            openBottomSheet()
        }

    }

    fun openBottomSheet(){
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!

                uri?.let { galleryUri ->
                    contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
        }

    fun getRoundedCornerBitmap(bitmap: Bitmap?): Bitmap? {
        val targetWidth = 100 // Set the desired width
        val targetHeight = 100 // Set the desired height
        val targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(targetBitmap)
        val paint = Paint()
        paint.setAntiAlias(true)
        paint.setShader(BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        val rectF = RectF(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat())
        canvas.drawRoundRect(rectF, targetWidth / 2f, targetHeight / 2f, paint)
        return targetBitmap
    }

    override fun onSuccess() {
        progressbarview?.visibility = View.GONE
        val resultIntent = Intent()
        resultIntent.putExtra("ItemAdded", "profileDetailsAdded")
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onFailure(errorMessage: String?) {

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

    override fun onClickOnCamera() {
        bottomSheetFragment.dismissNow()

        if (checkCameraPermission()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            requestCameraPermission()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            profileImage?.setImageBitmap(getRoundedCornerBitmap(imageBitmap))
            imageBitmap.let {
                cameraviewModel.uploadImage(it!!)
            }
        }
    }

    override fun onClickOnGallary() {
        bottomSheetFragment.dismissNow()
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        profileActivityResultLauncher.launch(pickImageIntent)
    }

    override fun closebtnAction() {
        bottomSheetFragment.dismissNow()
    }

    override fun onsuccessImageUpload(imageid: String?) {
        profileimageid = imageid
    }

    override fun onsavesuccessdrivinglicense() {

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
}
