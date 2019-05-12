package com.example.systemious.ui

import android.os.Bundle
import android.util.Log
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
import androidx.core.content.ContextCompat


class SystemStateActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var viewModel: ActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_state)

        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)

        setSupportActionBar(mainToolbar)
        initNavigation()
    }

    private fun initNavigation() {
        navController = Navigation.findNavController(this, R.id.mainFragment)
        bottomNavigation.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.navigationSystemState -> {
                    supportActionBar?.title = getString(R.string.system_state)
                }
                R.id.navigationSystemDetails -> {
                    supportActionBar?.title = getString(R.string.system_info)
                }
                R.id.navigationFileManager-> {
                    supportActionBar?.title = getString(R.string.file_manager)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.navServiceStateToggle -> viewModel.toggleServiceState()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        viewModel.isSystemServiceWorking.observe(this, Observer<Boolean> { isInfoServiceWorking ->
            val iconId = if (isInfoServiceWorking) R.drawable.ic_stop_secondary_color_24dp else R.drawable.ic_resume_secondary_24dp
            menu.getItem(0)?.icon = ContextCompat.getDrawable(this, iconId)
        })

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }
}
