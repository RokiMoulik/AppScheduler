package com.example.appscheduler.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.AppLaunchChecker
import androidx.core.content.ContextCompat.startActivity
import com.example.appscheduler.receiver.AppLaunchReceiver

class SchedulerUtil (private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setScheduleApp(packageName: String, scheduledTime: Long) {
        Log.d(TAG, "setScheduleApp -> scheduledTime: $scheduledTime")

        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            putExtra("packageName", packageName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            packageName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                setExactAlarm(scheduledTime, pendingIntent)
            } else {
                requestExactAlarmPermission()
            }
        } else {
            setExactAlarm(scheduledTime, pendingIntent)
        }
    }

    fun cancelSchedule(packageName: String) {
        val intent = Intent(context, AppLaunchReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            packageName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent) // Opens settings for the user to manually allow exact alarms
        }
    }

    private fun setExactAlarm(scheduledTime: Long, pendingIntent: PendingIntent) {
        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent)
        } catch (e: SecurityException) {
            Log.e(TAG, "Exact alarm permission not granted: ${e.message}")
            Toast.makeText(context, "Please enable exact alarms in settings", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val TAG = "SchedulerUtil"
    }
}