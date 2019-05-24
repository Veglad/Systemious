package com.example.systemious.ui.file_manager

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.systemious.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.net.URI
import java.text.DecimalFormat

class FileManagerRecyclerAdapter(private val context: Context, private val activity: Activity)
    : RecyclerView.Adapter<FileManagerRecyclerAdapter.ViewHolder>() {

    private val fileItemList = mutableListOf<FileItem>()
    private var onFileItemClickListener: ((fileItem: FileItem) -> Unit)? = null
    private var onFileDeleteSelectedListener: ((fileItem: FileItem) -> Unit)? = null

    fun setOnFileItemClickListener(onFileItemClickListener: (FileItem) -> Unit) {
        this.onFileItemClickListener = onFileItemClickListener
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
                holder.moreButton.visibility = View.GONE
            } else {
                holder.moreButton.visibility = View.VISIBLE
                loadIconToImageView(fileItem.iconUri, fileItem.type, fileIcon)
            }
            moreButton.setOnClickListener {
                showOptionsDialog(holder)
            }
        }
    }

    private fun loadIconToImageView(uri: Uri?, fileType: FileType, imageView: ImageView) {
        if (uri == null) {
            val drawable = if (fileType == FileType.DIRECTORY) {
                ContextCompat.getDrawable(context, R.drawable.ic_folder_secondary_24dp)
            } else {
                ContextCompat.getDrawable(context, R.drawable.ic_insert_drive_file_black_24dp)
            }
            imageView.setImageDrawable(drawable)
        } else {
            Glide.with(context)
                .load(uri)
                .into(imageView)
        }
    }

    private fun showOptionsDialog(holder: ViewHolder) {
        val optionsBottomSheetDialog = BottomSheetDialog(activity)
        val sheetView = activity.layoutInflater.inflate(R.layout.file_manager_item_options_bottom_sheet, null)
        optionsBottomSheetDialog.setContentView(sheetView)

        val deleteItem = sheetView.findViewById<View>(R.id.file_manager_delete_option)
        val infoItem = sheetView.findViewById<View>(R.id.file_manager_info_option)

        deleteItem.setOnClickListener {
            onFileDeleteSelectedListener?.invoke(fileItemList[holder.adapterPosition])
            optionsBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        infoItem.setOnClickListener {
            optionsBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN
            val fileItem = fileItemList[holder.adapterPosition]
            showInfoDialog(fileItem)
        }

        optionsBottomSheetDialog.show()
    }

    private fun showInfoDialog(fileItem: FileItem) {
        val infoBottomSheetDialog = BottomSheetDialog(activity)
        val sheetView = activity.layoutInflater.inflate(R.layout.file_manager_item_info_bottom_sheet, null)
        infoBottomSheetDialog.setContentView(sheetView)
        infoBottomSheetDialog.show()

        val icon = infoBottomSheetDialog.findViewById<ImageView>(R.id.fileManagerInfoImage)
        val name = infoBottomSheetDialog.findViewById<TextView>(R.id.fileManagerInfoName)
        val path = infoBottomSheetDialog.findViewById<TextView>(R.id.fileManagerInfoPath)
        val size = infoBottomSheetDialog.findViewById<TextView>(R.id.fileManagerInfoSize)
        val modified = infoBottomSheetDialog.findViewById<TextView>(R.id.fileManagerInfoModificationDate)
        val okButton = infoBottomSheetDialog.findViewById<TextView>(R.id.fileManagerInfoOkButton)

        val fileInfo = FileManagerAdapterHelper.getAdvancedFileInfo(fileItem)

        icon?.let { loadIconToImageView(fileInfo.iconUri, fileInfo.fileType, it) }
        name?.text = fileInfo.fileName
        path?.text = fileInfo.filePath
        val fileSize = DecimalFormat("#.##").format(fileInfo.fileSize)
        size?.text = "$fileSize ${fileInfo.fileSizeSuffix}"
        modified?.text = fileInfo.fileLastModificationDate
        okButton?.setOnClickListener { infoBottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_HIDDEN }
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