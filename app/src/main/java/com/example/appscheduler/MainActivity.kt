package com.example.appscheduler

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appscheduler.databinding.ActivityMainBinding
import com.example.appscheduler.data.AppDatabase
import com.example.appscheduler.data.entity.ScheduledApp
import com.example.appscheduler.ui.edit.AddEditActivity
import com.example.appscheduler.ui.main.MainContract
import com.example.appscheduler.ui.main.MainPresenter
import com.example.appscheduler.ui.main.ScheduledAppAdapter

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding : ActivityMainBinding
    private lateinit var adapter: ScheduledAppAdapter

    private val presenter: MainPresenter by lazy {
        val database = AppDatabase.getDatabase(applicationContext)
        MainPresenter(this, database.scheduledAppDao(), applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showOverlayPermissionDialog()

        // setup recyclerView
        adapter = ScheduledAppAdapter (
            onEditClick = {scheduledApp ->
                presenter.onEditButtonClick(scheduledApp) },
            onDeleteClick = {scheduledApp ->
                presenter.deleteScheduledApp(scheduledApp) },
            applicationContext)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Observe LiveData
        presenter.scheduledApps.observe(this, Observer { scheduledApps ->
            adapter.submitList(scheduledApps)
        })

        // Add button click listener
        binding.addButton.setOnClickListener {
            presenter.onAddButtonClick()
        }
    }

    override fun showScheduledApps(scheduledApps: List<ScheduledApp>) {
        TODO("Not yet implemented")
    }

    override fun showAddScreen() {
        val intent = Intent(this, AddEditActivity::class.java)
        startActivity(intent)
    }

    override fun showEditScreen(scheduledApp: ScheduledApp) {
        val intent = Intent(this, AddEditActivity::class.java).apply {
            putExtra("scheduledApp", scheduledApp)
        }
        startActivity(intent)
    }

    private fun showOverlayPermissionDialog() {
        if (Settings.canDrawOverlays(this)) {
            return
        }
        val dialog = AlertDialog.Builder(this)
        .setTitle("Overlay Permission Required")
        .setMessage("This app needs the Overlay permission to launch apps in the background. Please grant the permission.")
        .setPositiveButton("Grant Permission") { _, _ ->
            requestOverlayPermission()  // Request permission when the user agrees
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()  // Dismiss the dialog if the user cancels
        }
        .create()

        dialog.show()  // Show the dialog
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Overlay permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    }
}