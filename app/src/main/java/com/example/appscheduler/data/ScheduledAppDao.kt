package com.example.appscheduler.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.appscheduler.data.entity.ScheduledApp

@Dao
interface ScheduledAppDao {
    @Insert
    suspend fun insert(scheduledApp: ScheduledApp)

    @Update
    suspend fun update(scheduledApp: ScheduledApp)

    @Query("SELECT * FROM scheduled_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): ScheduledApp?

    @Query("SELECT * FROM scheduled_apps")
    fun getAll(): LiveData<List<ScheduledApp>>

    @Query("DELETE FROM scheduled_apps WHERE id = :id")
    suspend fun delete(id: Int)
}