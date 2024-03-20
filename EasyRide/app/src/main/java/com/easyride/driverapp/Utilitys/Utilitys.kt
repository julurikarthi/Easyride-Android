package com.easyride.driverapp.Utilitys

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.easyride.driverapp.Adapters.ApplicationContextProvider
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class Utilitys {

   fun saveBitmapToFile(bitmap: Bitmap, fileName: String): File? {
      var contextt = ApplicationContextProvider.getApplicationContext()
      val directory = File(contextt.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath)
      directory.mkdirs()

      val file = File(directory, fileName)

      try {
         val outputStream = FileOutputStream(file)
         bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
         outputStream.flush()
         outputStream.close()
         return file
      } catch (e: IOException) {
         e.printStackTrace()
      }
      return null
   }

   companion object {
      fun showSnackbar(view: View, message: String) {
         var snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
         snackbar.view.setBackgroundColor(Color.WHITE)
         val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
         textView.setTextColor(Color.BLACK)
         snackbar.show()
      }

      fun setupUI(view: View, activity: ComponentActivity) {
         // Set up touch listener on root view
         view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
               // Dismiss the keyboard when a touch event occurs outside of an input field
               hideKeyboard(activity)
            }
            false
         }
      }

       fun hideKeyboard(activity: ComponentActivity) {
         val imm = activity.getSystemService(ComponentActivity.INPUT_METHOD_SERVICE) as InputMethodManager
         imm.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
      }
   }



}