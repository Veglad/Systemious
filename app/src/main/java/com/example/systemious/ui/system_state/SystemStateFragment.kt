package com.example.systemious.ui.system_state

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.systemious.R

class SystemStateFragment : Fragment() {

    companion object {
        fun newInstance() = SystemStateFragment()
    }

    private lateinit var viewModel: SystemStateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.system_state_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SystemStateViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
