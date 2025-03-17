package com.example.appscheduler.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_apps")
data class ScheduledApp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packageName: String,
    val scheduledTime: Long,
    val isExecuted: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(packageName)
        parcel.writeLong(scheduledTime)
        parcel.writeByte(if (isExecuted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduledApp> {
        override fun createFromParcel(parcel: Parcel): ScheduledApp {
            return ScheduledApp(parcel)
        }

        override fun newArray(size: Int): Array<ScheduledApp?> {
            return arrayOfNulls(size)
        }
    }
}
