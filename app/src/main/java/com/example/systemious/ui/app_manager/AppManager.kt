package com.example.systemious.ui.app_manager

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.systemious.R
import kotlinx.android.synthetic.main.app_manager_fragment.*

class AppManager : Fragment() {

    companion object {
        fun newInstance() = AppManager()
    }

    private lateinit var viewModel: AppManagerViewModel
    private lateinit var appInfoAdapter: AppManagerRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AppManagerViewModel::class.java)
        initViewModels()
        initRecyclerView()
        appManagerSwipeRefresh.setOnRefreshListener { viewModel.loadAppInfo() }
    }

    private fun initViewModels() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            appManagerSwipeRefresh.isRefreshing = isLoading
        })
        viewModel.appsInfoList.observe(this, Observer { appsInfoList ->
            appInfoAdapter.updateAppInfoList(appsInfoList)
        })
    }

    private fun initRecyclerView() {
        activity?.baseContext?.let {context ->
            appInfoAdapter = AppManagerRecyclerAdapter(context)
            with(appManagerRecyclerView) {
                adapter = appInfoAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }
}
