package com.example.appscheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appscheduler.data.AppDatabase
import com.example.appscheduler.service.AppLaunchService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLaunchReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.getStringExtra("packageName")
        Log.i(TAG, "onReceive -> packageName: $packageName")

        packageName?.let {
            val serviceIntent = Intent(context, AppLaunchService::class.java).apply {
                    putExtra("packageName", packageName)
                }
            context?.startService(serviceIntent)

            CoroutineScope(Dispatchers.IO).launch {
                // update the database
                val database = context?.let { it1 -> AppDatabase.getDatabase(it1) }
                val scheduledAppDao = database?.scheduledAppDao()
                val scheduledApp = scheduledAppDao?.getByPackageName(packageName)
                scheduledApp?.let {
                    val updateSchedule = it.copy(
                        isExecuted = true
                    )
                    scheduledAppDao.update(updateSchedule)
                }
            }
        }
    }

    companion object {
        const val TAG = "AppLaunchReceiver"
    }

}