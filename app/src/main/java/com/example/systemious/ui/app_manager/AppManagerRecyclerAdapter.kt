package com.example.systemious.ui.app_manager

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


class AppManagerRecyclerAdapter(private val context: Context)
    : RecyclerView.Adapter<AppManagerRecyclerAdapter.ViewHolder>() {

    private val appInfoList = mutableListOf<AppInfo>()
    private var packageManager: PackageManager = context.packageManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.app_info_list_item, parent,false)
            return ViewHolder(itemView)
    }

    override fun getItemCount() = appInfoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appInfoList[position]
        with(holder) {
            appImage.setImageDrawable(appInfo.icon)
            nameTextView.text = appInfo.appName
            sizeTextView.text = String.format("%.2f MB", appInfo.sizeInMb)
            itemView.setOnClickListener {
                launchDetails(appInfo.packageName)
            }
        }
    }

    private fun launchDetails(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let {
            it.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(it)
        }
    }

    fun updateAppInfoList(appInfoList: List<AppInfo>) {
        this.appInfoList.clear()
        this.appInfoList.addAll(appInfoList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appImage: ImageView = itemView.findViewById(R.id.appInfoImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.appInfoNameTextView)
        val sizeTextView: TextView = itemView.findViewById(R.id.appInfoSizeTextView)
    }
}