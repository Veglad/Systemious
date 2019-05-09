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

class SystemStateActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var viewModel: ActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_state)

        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)

        setSupportActionBar(mainToolbar)
        initNavigation()

        viewModel.isSystemServiceWorking.observe(this, Observer<Boolean> { isInfoServiceWorking ->
           Log.d("Activity", "Isworking: $isInfoServiceWorking")
        })
    }

    private fun initNavigation() {
        navController = Navigation.findNavController(this, R.id.mainFragment)
        bottomNavigation.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }
}
