package com.easyride.driverapp.UIScreens.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.easyride.driverapp.R
import com.easyride.driverapp.Utilitys.ItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

interface MyBottomSheetFragmentProtocol {
    fun onClickOnCamera()
    fun onClickOnGallary()
    fun closebtnAction()
}

class MyBottomSheetFragment :BottomSheetDialogFragment() {

    var delegate: MyBottomSheetFragmentProtocol? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var cameraview = view.findViewById<LinearLayout>(R.id.cameraview)
        var gallaryView = view.findViewById<LinearLayout>(R.id.gallaryView)
        var closebtn = view.findViewById<ImageView>(R.id.closebtn)
        cameraview.setOnClickListener {
            delegate?.onClickOnCamera()
        }
        gallaryView.setOnClickListener {
            delegate?.onClickOnGallary()
        }
        closebtn.setOnClickListener {
            delegate?.closebtnAction()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog)
    }

    private fun showSnackbar() {
        Snackbar.make(requireView(), "Snackbar in Bottom Sheet", Snackbar.LENGTH_SHORT).show()
    }
}