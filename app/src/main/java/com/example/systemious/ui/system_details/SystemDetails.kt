package com.example.systemious.ui.system_details

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.systemious.R
import kotlinx.android.synthetic.main.system_details_fragment.*

class SystemDetails : Fragment() {

    companion object {
        fun newInstance() = SystemDetails()
    }

    private lateinit var viewModel: SystemDetailsViewModel
    private lateinit var systemParamsAdapter: SystemDetailsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.system_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SystemDetailsViewModel::class.java)
        systemDetailsSwipeRefresh.setOnRefreshListener { viewModel.loadSystemDetails() }
        initViewModels()
        initRecyclerView()
    }

    private fun initViewModels() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            systemDetailsSwipeRefresh.isRefreshing = isLoading
        })
        viewModel.systemDetailsList.observe(this, Observer { systemParamList ->
            systemParamsAdapter.updateAppInfoList(systemParamList)
        })
    }

    private fun initRecyclerView() {
        activity?.baseContext?.let {context ->
            with(systemDetailsRecyclerView) {
                systemParamsAdapter = SystemDetailsRecyclerAdapter(context)
                adapter = systemParamsAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

}
