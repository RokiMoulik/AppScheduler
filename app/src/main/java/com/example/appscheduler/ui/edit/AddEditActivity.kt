package com.example.appscheduler.ui.edit

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.size
import com.example.appscheduler.R
import com.example.appscheduler.databinding.ActivityAddEditBinding
import com.example.appscheduler.model.AppDatabase
import com.example.appscheduler.model.entity.ScheduledApp
import com.example.appscheduler.ui.main.AppSpinnerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

class AddEditActivity : AppCompatActivity() , AddEditContract.View {
    private lateinit var appSpinner: Spinner
    private lateinit var timePicker: TimePicker
    private lateinit var saveButton: Button

    private val presenter: AddEditPresenter by lazy {
        val dataset = AppDatabase.getDatabase(applicationContext)
        AddEditPresenter(this, dataset.scheduledAppDao(), applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        timePicker = findViewById(R.id.timePicker)
        saveButton = findViewById(R.id.saveButton)
        appSpinner = findViewById(R.id.appSpinner)

        // Populate the Spinner with installed apps
        populateAppSpinner()

        val scheduledApp = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("scheduledApp", ScheduledApp::class.java)
        } else {
            intent.getParcelableExtra<ScheduledApp>("scheduledApp")
        }

        scheduledApp?.let {
            presenter.loadSchedule(it)
        }

        saveButton.setOnClickListener {
            val packageInfo = appSpinner.selectedItem as ApplicationInfo
            val hour = timePicker.hour
            val minute = timePicker.minute

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val scheduledTime = calendar.timeInMillis

            presenter.saveSchedule(packageInfo.packageName, scheduledTime)
        }
    }

    override fun showSaveSuccess() {
        runOnUiThread {
            Toast.makeText(this, "The app has been successfully scheduled.", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun showSaveError() {
        runOnUiThread {
            Toast.makeText(this, "Failed to save schedule. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun populateUI(packageName: String, hour: Int, minute: Int) {
        appSpinner.setSelection(getAppIndex(packageName))
        timePicker.hour = hour
        timePicker.minute = minute
    }

    private fun getAppIndex(packageName: String): Int {
        val adapter = appSpinner.adapter as ArrayAdapter<ApplicationInfo>
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i)?.packageName.equals(packageName)) {
                return i
            }
        }
        return -1;
    }

    private fun populateAppSpinner() {
        val packageManager = packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val deviceInstalledApps = installedApps.filter { appInfo ->
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 // Exclude system apps
        }

        val adapter = AppSpinnerAdapter(this, deviceInstalledApps)
        appSpinner.adapter = adapter
    }

    companion object {
        const val TAG = "AddEditActivity"
    }
}