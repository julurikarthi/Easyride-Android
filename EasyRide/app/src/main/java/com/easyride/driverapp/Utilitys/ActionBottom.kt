package com.easyride.driverapp.Utilitys

import com.easyride.driverapp.UIScreens.Fragments.MyBottomSheetFragment

class ActionBottom {
    companion object{
        const val TAG = "ActionBottomDialog"
        fun newInstance():MyBottomSheetFragment{
            return MyBottomSheetFragment()
        }
    }

}