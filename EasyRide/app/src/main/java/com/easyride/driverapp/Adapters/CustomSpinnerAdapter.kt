package com.easyride.driverapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.easyride.driverapp.R
import com.easyride.driverapp.UIScreens.AddCarDetailsActivity

class CustomSpinnerAdapter(context: AddCarDetailsActivity, resource: Int, items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val customResource: Int = resource

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, parent, false)

        // Access the TextView and set its text
        val textView: TextView = itemView.findViewById(R.id.textView)
        textView.text = getItem(position)
        return itemView
    }
}