package com.example.systemious.ui.file_manager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.systemious.R
import androidx.appcompat.widget.PopupMenu


class FileManagerRecyclerAdapter(private val context: Context)
    : RecyclerView.Adapter<FileManagerRecyclerAdapter.ViewHolder>() {

    private val fileItemList = mutableListOf<FileItem>()
    private var onFileItemClickListener: ((fileItem: FileItem) -> Unit)? = null
    private var onInfoSelectedListener: ((fileItem: FileItem) -> Unit)? = null
    private var onFileDeleteSelectedListener: ((fileItem: FileItem) -> Unit)? = null

    fun setOnFileItemClickListener(onFileItemClickListener: (FileItem) -> Unit) {
        this.onFileItemClickListener = onFileItemClickListener
    }

    fun setOnInfoSelectedListener(onInfoSelectedListener: (FileItem) -> Unit) {
        this.onInfoSelectedListener = onInfoSelectedListener
    }

    fun setOnFileDeleteSelectedListener(onFileDeleteSelectedListener: (FileItem) -> Unit) {
        this.onFileDeleteSelectedListener = onFileDeleteSelectedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.file_manager_list_item, parent,false)
            return ViewHolder(itemView)
    }

    override fun getItemCount() = fileItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileItem = fileItemList[position]
        with(holder) {
            itemView.setOnClickListener {
                onFileItemClickListener?.invoke(fileItemList[holder.adapterPosition])
            }

            fileName.text = fileItem.name

            if (fileItem.type == FileType.PARENT_FOLDER) {
                fileIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_folder_secondary_24dp))
            } else {
                if (fileItem.iconUri == null) {
                    val drawable = if (fileItem.type == FileType.DIRECTORY) {
                        ContextCompat.getDrawable(context, R.drawable.ic_folder_secondary_24dp)
                    } else {
                        ContextCompat.getDrawable(context, R.drawable.ic_insert_drive_file_black_24dp)
                    }
                    fileIcon.setImageDrawable(drawable)
                } else {
                    Glide.with(context)
                        .load(fileItem.iconUri)
                        .into(fileIcon)
                }
            }
            moreButton.setOnClickListener {
                popUpMenu(holder)
            }
        }
    }

    private fun popUpMenu(holder: ViewHolder) {
        val popup = PopupMenu(context, holder.moreButton)
        popup.inflate(R.menu.file_more_menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.infoFileManagerMoreItem -> {
                    onInfoSelectedListener?.invoke(fileItemList[holder.adapterPosition])
                    true
                }

                R.id.deleteFileManagerMoreItem -> {
                    onFileDeleteSelectedListener?.invoke(fileItemList[holder.adapterPosition])
                    true
                }
                else -> {false}
            }
        }
        popup.show()
    }

    fun updateAppInfoList(fileItemList: List<FileItem>) {
        this.fileItemList.clear()
        this.fileItemList.addAll(fileItemList)
        notifyDataSetChanged()
    }

    fun removeItem(fileItem: FileItem) {
        val position = fileItemList.indexOf(fileItem)
        fileItemList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileIcon: ImageView = itemView.findViewById(R.id.fileManagerIcon)
        val fileName: TextView = itemView.findViewById(R.id.fileManagerFileNameTextView)
        val moreButton: ImageButton = itemView.findViewById(R.id.fileManagerMoreImageButton)
    }
}