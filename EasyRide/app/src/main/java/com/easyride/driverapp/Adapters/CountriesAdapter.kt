package com.easyride.driverapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.easyride.driverapp.LoginSinnupViewModel.Country
import com.easyride.driverapp.R

class CountriesAdapter(context: Context, val items: List<Country>) :
    ArrayAdapter<Country>(context, 0, items) {
    var selectedposition: Int = 0
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)

        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, parent, false)

        val selectedtick: ImageView = itemView.findViewById(R.id.selectedtick)
        val countyimage: TextView = itemView.findViewById(R.id.countyimage)
        val countryTextView: TextView = itemView.findViewById(R.id.countytext)
        val currentItem = getItem(position)
        countyimage.text = currentItem?.emoji
        countryTextView.text = currentItem?.name
        if (this.selectedposition == position) {
            selectedtick.visibility = View.VISIBLE
        } else {
            selectedtick.visibility = View.GONE
        }

        return itemView
    }

}
