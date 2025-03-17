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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingSchedule = scheduledAppDao.getByPackageName(packageName)
                if (existingSchedule != null) {

                    schedulerUtil.cancelSchedule(packageName)

                    val updateSchedule = existingSchedule.copy(
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