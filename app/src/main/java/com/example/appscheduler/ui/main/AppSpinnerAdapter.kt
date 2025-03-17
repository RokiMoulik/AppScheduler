package com.example.appscheduler.ui.main

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.appscheduler.R

class AppSpinnerAdapter(
    context: Context,
    private val apps: List<ApplicationInfo>
) : ArrayAdapter<ApplicationInfo> (context, 0, apps) {

    val packageManager = context.packageManager

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_item, parent, false)

        val item = getItem(position)!!
        val spinnerAppIcon: ImageView = view.findViewById(R.id.spinnerAppIcon)
        val spinnerAppName: TextView = view.findViewById(R.id.spinnerAppName)

        spinnerAppIcon.setImageDrawable(packageManager.getApplicationIcon(item.packageName))
        spinnerAppName.text = packageManager.getApplicationLabel(item).toString()

        return view
    }
}