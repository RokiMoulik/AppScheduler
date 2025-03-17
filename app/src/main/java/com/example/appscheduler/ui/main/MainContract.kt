package com.example.appscheduler.ui.main

import com.example.appscheduler.data.entity.ScheduledApp

interface MainContract {
    interface View {
        fun showScheduledApps(scheduledApps: List<ScheduledApp>)
        fun showAddScreen()
        fun showEditScreen(scheduledApp: ScheduledApp)
    }

    interface Presenter {
        fun loadScheduledApps()
        fun deleteScheduledApp(scheduledApp: ScheduledApp)
        fun onAddButtonClick()
        fun onEditButtonClick(scheduledApp: ScheduledApp)
    }
}