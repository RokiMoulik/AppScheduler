package com.example.appscheduler

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appscheduler.databinding.ActivityMainBinding
import com.example.appscheduler.model.AppDatabase
import com.example.appscheduler.model.entity.ScheduledApp
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
}