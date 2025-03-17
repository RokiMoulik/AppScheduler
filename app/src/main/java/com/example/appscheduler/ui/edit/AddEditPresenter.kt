package com.example.appscheduler.ui.edit

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import com.example.appscheduler.data.ScheduledAppDao
import com.example.appscheduler.data.entity.ScheduledApp
import com.example.appscheduler.util.SchedulerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEditPresenter(
    private val view: AddEditContract.View,
    private val scheduledAppDao: ScheduledAppDao,
    private val context: Context
) : AddEditContract.Presenter {

    private val schedulerUtil = SchedulerUtil(context)

    override fun loadSchedule(scheduledApp: ScheduledApp?) {
        scheduledApp?.let {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = it.scheduledTime
            }

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            view.populateUI(it.packageName, hour, minute)
        }
    }

    override fun saveSchedule(packageName: String, scheduledTime: Long) {
        Log.d(TAG, "saveSchedule: packageName: $packageName , scheduledTime: $scheduledTime")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingPackageSchedule = scheduledAppDao.getByPackageName(packageName)
                val existingTimeSchedule = scheduledAppDao.getByScheduledTime(scheduledTime)

                Log.d(TAG, "saveSchedule: existing time: ${existingTimeSchedule?.scheduledTime}")

                if (existingTimeSchedule != null) {
                    view.showSaveError()
                    return@launch
                } else if (existingPackageSchedule != null) {
                    schedulerUtil.cancelSchedule(packageName)
                    val updateSchedule = existingPackageSchedule.copy(
                        scheduledTime = scheduledTime,
                        isExecuted = false
                    )
                    scheduledAppDao.update(updateSchedule)
                } else {
                    val scheduledApp = ScheduledApp(
                        packageName = packageName,
                        scheduledTime = scheduledTime, isExecuted = false
                    )
                    scheduledAppDao.insert(scheduledApp)
                }
                // Schedule the app launch using SchedulerUtil
                schedulerUtil.setScheduleApp(packageName, scheduledTime)

                view.showSaveSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "saveSchedule: $e")
                view.showSaveError()
            }
        }
    }

    companion object {
        const val TAG = "AddEditPresenter"
    }
}