package com.example.systemious.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.systemious.R
import kotlinx.android.synthetic.main.activity_system_state.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.systemious.data.repository.Repository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var viewModel: ActivityViewModel

    private lateinit var reportBottomDialog: BottomSheetDialog

    private lateinit var reportProgressBar: ProgressBar
    private lateinit var reportProgressTextView: TextView
    private lateinit var reportErrorTextView: TextView
    private lateinit var reportPathTextView: TextView
    private lateinit var reportSuccessTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_state)

        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        initViewModelObservers()

        setSupportActionBar(mainToolbar)
        initNavigation()
    }

    private fun initViewModelObservers() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            setShowIsLoading(isLoading)
        })
        viewModel.error.observe(this, Observer { ex ->
            showReportError()
        })
        viewModel.reportCsvFile.observe(this, Observer { file ->
            setShowIsLoading(false)
            if (file == null) {
                showReportError()
            } else {
                reportSuccessTextView.visibility = View.VISIBLE
                reportPathTextView.visibility = View.VISIBLE
                reportErrorTextView.visibility = View.GONE

                reportPathTextView.text = "Path: ${file.absoluteFile}"
                reportPathTextView.setOnClickListener {
                    startActivity(Intent(Repository.getOpenFileIntent(file, this)))
                }
            }
        })
    }

    private fun showReportError() {
        setShowIsLoading(false)
        reportErrorTextView.visibility = View.VISIBLE
    }

    private fun setShowIsLoading(isLoading: Boolean) {
        if (isLoading) {
            reportProgressBar.visibility = View.VISIBLE
            reportProgressTextView.visibility = View.VISIBLE

            reportErrorTextView.visibility = View.GONE

            reportSuccessTextView.visibility = View.GONE
            reportPathTextView.visibility = View.VISIBLE
        } else {
            reportProgressBar.visibility = View.GONE
            reportProgressTextView.visibility = View.GONE
        }
    }

    private fun initNavigation() {
        navController = Navigation.findNavController(this, R.id.mainFragment)
        bottomNavigation.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigationSystemState -> {
                    supportActionBar?.title = getString(R.string.system_state)
                }
                R.id.navigationSystemDetails -> {
                    supportActionBar?.title = getString(R.string.system_info)
                }
                R.id.navigationFileManager -> {
                    supportActionBar?.title = getString(R.string.file_manager)
                }
                else -> supportActionBar?.title = getString(R.string.apps_manager)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navServiceStateToggle -> viewModel.toggleServiceState()
            R.id.navClearStorage -> showClearStorageDialog()
            R.id.navMakeReport -> showReportDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showClearStorageDialog() {
        val optionsBottomSheetDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.clear_storage_bottom_sheet, null)
        optionsBottomSheetDialog.setContentView(sheetView)

        val cancel = sheetView.findViewById<View>(R.id.clear_storage_cancel_item)
        val delete = sheetView.findViewById<View>(R.id.clear_storage_delete_item)

        cancel.setOnClickListener {
            optionsBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        delete.setOnClickListener {
            optionsBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.clearStorage()
        }

        optionsBottomSheetDialog.show()
    }

    private fun showReportDialog() {
        reportBottomDialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.report_bottom_sheet, null)

        reportProgressBar = sheetView.findViewById(R.id.reportProgressBar)
        reportProgressTextView = sheetView.findViewById(R.id.reportProgressTextView)
        reportErrorTextView = sheetView.findViewById(R.id.reportErrorTextView)
        reportPathTextView = sheetView.findViewById(R.id.reportPathTextView)
        reportSuccessTextView = sheetView.findViewById(R.id.reportSuccessTextView)

        reportBottomDialog.setContentView(sheetView)
        reportBottomDialog.show()
        viewModel.makeReport()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        viewModel.isSystemServiceWorking.observe(this, Observer<Boolean> { isInfoServiceWorking ->
            val iconId =
                if (isInfoServiceWorking) R.drawable.ic_stop_secondary_color_24dp else R.drawable.ic_resume_secondary_24dp
            menu.getItem(0)?.icon = ContextCompat.getDrawable(this, iconId)
        })

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }
}
