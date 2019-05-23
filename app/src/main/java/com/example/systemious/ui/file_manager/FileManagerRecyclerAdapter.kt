package com.example.systemious.ui.file_manager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.systemious.R


class FileManagerRecyclerAdapter(private val context: Context)
    : RecyclerView.Adapter<FileManagerRecyclerAdapter.ViewHolder>() {

    private val fileItemList = mutableListOf<FileItem>()
    private var onFileItemClickListener: ((fileItem: FileItem) -> Unit)? = null

    fun setOnFileItemClickListener(onFileItemClickListener: (FileItem) -> Unit) {
        this.onFileItemClickListener = onFileItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.file_manager_list_item, parent,false)
            return ViewHolder(itemView)
    }

    override fun getItemCount() = fileItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem = fileItemList[position]
        with(holder) {
            fileName.text = fileItem.name
            fileSize.text = "${fileItem.size} ${fileItem.sizeSuffix}"

            if (fileItem.icon == null) {
                val drawable = if (fileItem.isDirectory) {
                    ContextCompat.getDrawable(context, R.drawable.ic_folder_secondary_24dp)
                } else {
                    ContextCompat.getDrawable(context, R.drawable.ic_insert_drive_file_black_24dp)
                }
                fileIcon.setImageDrawable(drawable)
            } else {
                fileIcon.setImageBitmap(fileItem.icon)
            }
            itemView.setOnClickListener {
                onFileItemClickListener?.invoke(fileItemList[holder.adapterPosition])
            }
        }
    }

    fun updateAppInfoList(fileItemList: List<FileItem>) {
        this.fileItemList.clear()
        this.fileItemList.addAll(fileItemList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileIcon: ImageView = itemView.findViewById(R.id.fileManagerIcon)
        val fileName: TextView = itemView.findViewById(R.id.fileManagerFileNameTextView)
        val fileSize: TextView = itemView.findViewById(R.id.fileManagerFileSizeTextView)
    }
}