package com.example.appscheduler.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appscheduler.model.entity.ScheduledApp

@Database(entities = [ScheduledApp::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun scheduledAppDao(): ScheduledAppDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_scheduler_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}