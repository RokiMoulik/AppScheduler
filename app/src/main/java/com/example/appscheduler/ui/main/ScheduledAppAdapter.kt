package com.example.appscheduler.ui.main

import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appscheduler.R
import com.example.appscheduler.data.entity.ScheduledApp

class ScheduledAppAdapter (
    private val onEditClick: (ScheduledApp) -> Unit,
    private val onDeleteClick: (ScheduledApp) -> Unit,
    private val context: Context
) :ListAdapter<ScheduledApp, ScheduledAppAdapter.ViewHolder> (DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_scheduled_app, parent, false
        )
        return ViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheduledApp = getItem(position)
        holder.bind(scheduledApp, onEditClick, onDeleteClick)
    }

    class ViewHolder(itemView: View, private val context: Context): RecyclerView.ViewHolder(itemView) {
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val scheduledTime: TextView = itemView.findViewById(R.id.scheduledTime)
        private val editButton: Button = itemView.findViewById(R.id.editButton)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val isExecuted: TextView = itemView.findViewById(R.id.appExecuted)


        fun bind(
            scheduledApp: ScheduledApp,
            onEditClick: (ScheduledApp) -> Unit,
            onDeleteClick: (ScheduledApp) -> Unit
        ) {
            editButton.setOnClickListener{onEditClick(scheduledApp)}
            deleteButton.setOnClickListener { onDeleteClick(scheduledApp) }
            isExecuted.text = scheduledApp.isExecuted.toString()

            if (scheduledApp.isExecuted) {
                isExecuted.text = "Status: Executed"
            } else {
                isExecuted.text = "Status: Pending"
            }

            val packageName = scheduledApp.packageName
            val packageManager = context.packageManager
            appName.text = getAppName(packageName, packageManager)

            //set icon
            try {
                val icon = packageManager.getApplicationIcon(scheduledApp.packageName)
                appIcon.setImageDrawable(icon)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            val calendar = Calendar.getInstance().apply {
                timeInMillis = scheduledApp.scheduledTime
            }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            scheduledTime.text = "Scheduled Time: $hour:$minute"
        }

        private fun getAppName(packageName: String, packageManager: PackageManager): String {
            return try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "Not Found"
            }
        }

    }

    class DiffCallback: DiffUtil.ItemCallback<ScheduledApp> () {
        override fun areItemsTheSame(oldItem: ScheduledApp, newItem: ScheduledApp): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ScheduledApp, newItem: ScheduledApp): Boolean {
            return oldItem == newItem
        }
    }
}