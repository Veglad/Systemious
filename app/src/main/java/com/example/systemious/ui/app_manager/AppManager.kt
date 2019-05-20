package com.example.systemious.ui.app_manager

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.systemious.R

class AppManager : Fragment() {

    companion object {
        fun newInstance() = AppManager()
    }

    private lateinit var viewModel: AppManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AppManagerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
