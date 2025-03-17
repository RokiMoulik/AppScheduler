package com.example.appscheduler.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.appscheduler.model.ScheduledAppDao
import com.example.appscheduler.model.entity.ScheduledApp
import com.example.appscheduler.util.SchedulerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPresenter(
    private val view: MainContract.View,
    private val scheduledAppDao: ScheduledAppDao,
    private val context: Context
) : MainContract.Presenter {

    val scheduledApps: LiveData<List<ScheduledApp>> get() = scheduledAppDao.getAll()
    private val schedulerUtil = SchedulerUtil(context)

    override fun loadScheduledApps() {
        CoroutineScope(Dispatchers.IO).launch {
            // No need this function because LiveData will be updated automatically
        }
    }

    override fun deleteScheduledApp(scheduledApp: ScheduledApp) {
        CoroutineScope(Dispatchers.IO).launch {
            schedulerUtil.cancelSchedule(scheduledApp.packageName)
            scheduledAppDao.delete(scheduledApp.id)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "The scheduled has been deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAddButtonClick() {
        view.showAddScreen()
    }

    override fun onEditButtonClick(scheduledApp: ScheduledApp) {
        view.showEditScreen(scheduledApp)
    }
}