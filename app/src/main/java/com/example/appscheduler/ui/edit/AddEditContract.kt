package com.example.appscheduler.ui.edit

import com.example.appscheduler.data.entity.ScheduledApp

interface AddEditContract {
    interface View {
        fun showSaveSuccess()
        fun showSaveError()
        fun populateUI(packageName: String, hour: Int, minute: Int)
    }

    interface Presenter {
        fun loadSchedule(scheduledApp: ScheduledApp?) // Load schedule data
        fun saveSchedule(packageName: String, scheduledTime: Long)
    }

}