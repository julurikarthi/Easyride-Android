package com.easyride.driverapp.UIScreens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.Fragments.MyBottomSheetFragment
import com.easyride.driverapp.viewmodels.ProfileDetailsDelegate
import com.easyride.driverapp.viewmodels.ProfileDetailsViewModel

class ProfileDetailsActivity : ComponentActivity(), ProfileDetailsDelegate {
    var profileImage: ImageView? = null
    var fulladress: EditText? = null
    var saveprofilebtn: Button? = null
    var viewModel = ProfileDetailsViewModel()
    var progressbarview: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profiledetailsactivity)
        profileImage = findViewById(R.id.profileImage)
        fulladress = findViewById(R.id.fulladress)
        saveprofilebtn = findViewById(R.id.saveprofilebtn)
        progressbarview = findViewById(R.id.progressbarview)
        viewModel.delegate = this
        actionBar?.title = "Profile Details"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setElevation(0f)
        saveprofilebtn?.setOnClickListener {
            progressbarview?.visibility = View.VISIBLE
            viewModel.saveProfileDetails("test","temle")
        }

    }
    private fun showBottomSheet() {
        val bottomSheetFragment = MyBottomSheetFragment()
//        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
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

    fun openGalary() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

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
}
