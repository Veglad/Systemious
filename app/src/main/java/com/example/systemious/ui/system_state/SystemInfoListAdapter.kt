package com.example.systemious.ui.system_state

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.systemious.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.system_state_fragment.*
import java.util.*

class SystemInfoListAdapter(private val context: Context) : RecyclerView.Adapter<SystemInfoListAdapter.BaseViewholder>() {

    companion object {
        private const val ITEMS_NUMBER = 3
        private const val BATTERY_VIEW_TYPE = 0
        private const val MEMORY_VIEW_TYPE = 1
        private const val CPU_LIST_VIEW_TYPE = 2

        private const val BATTERY_VIEW_TYPE_POSITION = 0
        private const val MEMORY_VIEW_TYPE_POSITION = 1
        private const val CPU_LIST_VIEW_TYPE_POSITION = 2
    }

    private var batteryPercentage: Int = 100
    private var ramQueue: Queue<Long> = LinkedList()
    private var cpuUsagesQueue: MutableList<Queue<Double>> = mutableListOf()
    private val cpuUsageRecyclerAdapter by lazy {
        context?.let { CpuUsageRecyclerAdapter(mutableListOf(), it)}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemInfoListAdapter.BaseViewholder {
        val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.cpu_core_info_list_item, parent,false)
        return when(viewType) {
            BATTERY_VIEW_TYPE -> {
                val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.battery_layout_item, parent,false)
                BatteryViewHolder(itemView)
            }
            MEMORY_VIEW_TYPE -> {
                val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.memory_layout_item, parent,false)
                MemoryViewHolder(itemView)
            }
            else -> {
                val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.cpu_usages_list_layout_item, parent,false)
                CpuUsagesViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            BATTERY_VIEW_TYPE_POSITION -> BATTERY_VIEW_TYPE
            MEMORY_VIEW_TYPE_POSITION -> MEMORY_VIEW_TYPE
            else -> CPU_LIST_VIEW_TYPE
        }
    }

    override fun getItemCount() = ITEMS_NUMBER //Battery, memory view, cpu usages view

    override fun onBindViewHolder(holder: BaseViewholder, position: Int) {
        when (getItemViewType(position)) {
            BATTERY_VIEW_TYPE -> {
                val batteryViewHolder = holder as BatteryViewHolder
                batteryViewHolder.batteryProgress.progress = batteryPercentage
                batteryViewHolder.batteryLevelTextView.text =
                    String.format(context.getString(com.example.systemious.R.string.battery_level_format), batteryPercentage)
            }
            MEMORY_VIEW_TYPE -> {
                val memoryViewHolder = holder as MemoryViewHolder
                drawChartLines(ramQueue, memoryViewHolder.memoryChart)
            }
            else -> {
                val cpuUsagesViewHolder = holder as CpuUsagesViewHolder
                cpuUsagesViewHolder.recyclerView.adapter = cpuUsageRecyclerAdapter
                cpuUsagesViewHolder.recyclerView.layoutManager = LinearLayoutManager(context)
                cpuUsageRecyclerAdapter.updateCpuCoresUsages(cpuUsagesQueue)
            }
        }
    }

    fun updateBatteryPercentage(batteryPercentage: Int) {
        this.batteryPercentage = batteryPercentage
        notifyItemChanged(BATTERY_VIEW_TYPE_POSITION)
    }

    fun updateCpuCoresUsages(cpuCoreUsages: List<Queue<Double>>) {
        this.cpuUsagesQueue.clear()
        this.cpuUsagesQueue.addAll(cpuCoreUsages)
        notifyItemChanged(CPU_LIST_VIEW_TYPE_POSITION)
    }

    fun updateMemoryChart(ramQueue: Queue<Long>) {
        this.ramQueue.clear()
        this.ramQueue.addAll(ramQueue)
        notifyItemChanged(MEMORY_VIEW_TYPE_POSITION)
    }

    private fun drawChartLines(usedRamValueQueue: Queue<Long>, chart: LineChart) {
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
            lineDataSet = LineDataSet(seriesData,  context.getString(R.string.memory_line_data_set_title))
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

    open class BaseViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class BatteryViewHolder(itemView: View) : BaseViewholder(itemView) {
        val batteryProgress = itemView.findViewById<ProgressBar>(R.id.batteryProgress)
        val batteryLevelTextView = itemView.findViewById<TextView>(R.id.batteryLevelTextView)
    }

    class MemoryViewHolder(itemView: View) : BaseViewholder(itemView) {
        val memoryChart = itemView.findViewById<LineChart>(R.id.memoryChart)
    }

    class CpuUsagesViewHolder(itemView: View) : BaseViewholder(itemView) {
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.cpuUsageRecyclerView)
    }
}