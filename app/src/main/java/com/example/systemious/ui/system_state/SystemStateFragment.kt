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
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData

class SystemStateFragment : Fragment() {

    companion object {
        fun newInstance() = SystemStateFragment()
    }

    private lateinit var viewModel: SystemStateViewModel
    private var maxRamCapacity: Float = 0f
    private var coresNumber: Int = 1
    private val cpuUsageRecyclerAdapter by lazy {
        activity?.baseContext?.let { CpuUsageRecyclerAdapter(mutableListOf(), it)}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.system_state_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initCpuRecyclerView()

        viewModel = ViewModelProviders.of(this).get(SystemStateViewModel::class.java)
        viewModel.ramUsed.observe(this, Observer<Queue<Long>> { usedRamValueQueue ->
            drawChartLines(usedRamValueQueue, getString(R.string.memory_line_data_set_title), memoryChart)
        })
        viewModel.maxRamCapacity.observe(this,  Observer<Long> { maxRamCapacity ->
            this@SystemStateFragment.maxRamCapacity = maxRamCapacity.toFloat() })
        viewModel.coresNumber.observe(this, Observer<Int> { cores -> this.coresNumber = cores})
        viewModel.cpuUsages.observe(this, Observer<MutableList<Queue<Double>>> { usageQueuesList ->
            cpuUsageRecyclerAdapter?.updateCpuCoresUsages(usageQueuesList)
        })
        viewModel.batteryPercentage.observe(this, Observer<Int> { batteryPercentage ->
            batteryProgress.progress = batteryPercentage
            batteryLevelTextView.text = String.format(getString(com.example.systemious.R.string.battery_level_format), batteryPercentage)
        })
    }

    private fun initCpuRecyclerView() {
        cpuUsageRecyclerView.adapter = cpuUsageRecyclerAdapter
        cpuUsageRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun drawChartLines(usedRamValueQueue: Queue<Long>, lineDataSetTitle: String, chart: LineChart) {
        val seriesData = ArrayList<Entry>()

        for ((i, ramValue) in usedRamValueQueue.withIndex()) {
            seriesData.add(Entry(i.toFloat(), ramValue.toFloat()))
        }

        val lineDataSet: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            lineDataSet = chart.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = seriesData
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            lineDataSet = LineDataSet(seriesData, lineDataSetTitle)
            lineDataSet.setDrawIcons(false)
            lineDataSet.color = context?.let { ContextCompat.getColor(it, R.color.secondary_color) } ?: Color.DKGRAY
            lineDataSet.setCircleColor(Color.DKGRAY)
            lineDataSet.lineWidth = 1f
            lineDataSet.circleRadius = 2f
            lineDataSet.setDrawCircleHole(false)
            chart.description.isEnabled = false

            chart.axisRight.setDrawLabels(false)
            chart.xAxis.setDrawLabels(false)
        }

        lineDataSet.setDrawValues(false)
        chart.data = LineData(lineDataSet)
        chart.invalidate()
    }
}
