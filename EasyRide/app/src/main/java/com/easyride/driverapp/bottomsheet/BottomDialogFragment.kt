package com.easyride.driverapp.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.easyride.driverapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface BottomDialogFragmentdelegate {
    fun onCloseBtn()
    fun onTapOnlinebtn()
}
class BottomDialogFragment: BottomSheetDialogFragment() {
    var delegate: BottomDialogFragmentdelegate? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(R.layout.homebottomsheet, container, false)
        var imagview = view.findViewById<ImageView>(R.id.closebtn_dailog)
        var onlinebtn = view.findViewById<LinearLayout>(R.id.onlinebtn)
        imagview.setOnClickListener {
            delegate?.onCloseBtn()
        }
        onlinebtn.setOnClickListener {
            delegate?.onTapOnlinebtn()
        }
        return view
    }
}