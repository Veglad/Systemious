package com.example.systemious.ui.system_state

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.systemious.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.*

class CpuUsageRecyclerAdapter(private var cpuCoreUsages: MutableList<Queue<Double>>, private val context: Context)
    : RecyclerView.Adapter<CpuUsageRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.cpu_core_info_list_item, parent,false)
            return ViewHolder(itemView)
    }

    override fun getItemCount() = cpuCoreUsages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        drawChartLines(cpuCoreUsages[position], "$position core", holder.chart)
    }

    fun updateCpuCoresUsages(cpuCoreUsages: List<Queue<Double>>) {
        this.cpuCoreUsages.clear()
        this.cpuCoreUsages.addAll(cpuCoreUsages)
        notifyDataSetChanged()
    }

    private fun drawChartLines(usedRamValueQueue: Queue<Double>, lineDataSetTitle: String, chart: LineChart) {
        val seriesData = ArrayList<Entry>()

        for ((i, ramValue) in usedRamValueQueue.withIndex()) {
            seriesData.add(Entry(i.toFloat(), ramValue.toFloat()))
        }

        val lineDataSet: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            lineDataSet = chart.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = seriesData
            lineDataSet.label = lineDataSetTitle
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            lineDataSet = LineDataSet(seriesData, lineDataSetTitle)
            lineDataSet.setDrawIcons(false)
            lineDataSet.color =  ContextCompat.getColor(context, R.color.secondary_color)
            lineDataSet.setCircleColor(Color.DKGRAY)
            lineDataSet.lineWidth = 1f
            lineDataSet.circleRadius = 2f
            lineDataSet.setDrawCircleHole(false)
            chart.description.isEnabled = false

            chart.axisRight.setDrawLabels(false)
            chart.xAxis.setDrawLabels(false)

            val valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("%.3f%%", value * 100)
                }
            }

            chart.axisLeft.valueFormatter = valueFormatter
            chart.axisRight.valueFormatter = valueFormatter
        }

        lineDataSet.setDrawValues(false)
        chart.data = LineData(lineDataSet)
        chart.invalidate()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chart: LineChart = itemView.findViewById(R.id.cpuCoreListLineChart)
    }
}