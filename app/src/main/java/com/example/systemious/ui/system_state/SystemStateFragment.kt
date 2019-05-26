package com.example.systemious.ui.system_state

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.systemious.R
import kotlinx.android.synthetic.main.system_state_fragment.*
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator


class SystemStateFragment : Fragment() {

    companion object {
        fun newInstance() = SystemStateFragment()
    }

    private lateinit var viewModel: SystemStateViewModel
    private var maxRamCapacity: Float = 0f
    private var coresNumber: Int = 1
    private var systemInfoListAdapter: SystemInfoListAdapter? = null

    private var isRecyclerInit = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.system_state_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SystemStateViewModel::class.java)

        viewModel.coresNumber.observe(this, Observer<Int> { cores ->
            this.coresNumber = cores
            if (!isRecyclerInit) {
                isRecyclerInit = true
                initCpuRecyclerView()
            }
            initViewModelObservers()
        })
    }

    private fun initViewModelObservers() {
        viewModel.ramUsed.observe(this, Observer<Queue<Double>> { usedRamValueQueue ->
            systemInfoListAdapter?.updateMemoryChart(usedRamValueQueue)
        })
        viewModel.maxRamCapacity.observe(this, Observer<Long> { maxRamCapacity ->
            this@SystemStateFragment.maxRamCapacity = maxRamCapacity.toFloat()
        })
        viewModel.cpuUsages.observe(this, Observer<MutableList<Queue<Double>>> { usageQueuesList ->
            systemInfoListAdapter?.updateCpuCoresUsages(usageQueuesList)
        })
        viewModel.batteryPercentage.observe(this, Observer<Int> { batteryPercentage ->
            systemInfoListAdapter?.updateBatteryPercentage(batteryPercentage)
        })
    }

    private fun initCpuRecyclerView() {
        activity?.baseContext?.let { systemInfoListAdapter = SystemInfoListAdapter(it, coresNumber) }
        systemInfoMainRecyclerView.adapter = systemInfoListAdapter
        systemInfoMainRecyclerView.layoutManager = LinearLayoutManager(context)
        (systemInfoMainRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}
