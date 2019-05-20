package com.example.systemious.ui.system_details

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.systemious.R
import com.example.systemious.ui.app_manager.entities.AppInfo
import com.example.systemious.ui.system_details.entities.SystemParameter


class SystemDetailsRecyclerAdapter(private val context: Context)
    : RecyclerView.Adapter<SystemDetailsRecyclerAdapter.ViewHolder>() {

    private val systemDetailsList = mutableListOf<SystemParameter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.system_details_list_item, parent,false)
            return ViewHolder(itemView)
    }

    override fun getItemCount() = systemDetailsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val systemDetails = systemDetailsList[position]
        with(holder) {
            parameterName.text = systemDetails.parameterName
            parameterValue.text = systemDetails.parameterValue
        }
    }

    fun updateAppInfoList(systemDetailsList: List<SystemParameter>) {
        this.systemDetailsList.clear()
        this.systemDetailsList.addAll(systemDetailsList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parameterName: TextView = itemView.findViewById(R.id.systemDetailsParamNameTextView)
        val parameterValue: TextView = itemView.findViewById(R.id.systemDetailsParamValueTextView)
    }
}