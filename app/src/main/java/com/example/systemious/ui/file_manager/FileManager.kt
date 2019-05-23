package com.example.systemious.ui.file_manager

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.systemious.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.file_manager_fragment.*

class FileManager : Fragment() {

    companion object {
        fun newInstance() = FileManager()
    }

    private lateinit var viewModel: FileManagerViewModel
    private var fileAdapter: FileManagerRecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FileManagerViewModel::class.java)
        initRecyclerView()
        initViewModels()
    }

    private fun initRecyclerView() {
        activity?.baseContext?.let { context ->
            with(filesRecyclerView) {
                layoutManager = LinearLayoutManager(context)
                fileAdapter = FileManagerRecyclerAdapter(context)
                adapter = fileAdapter
            }
        }

        fileAdapter?.setOnFileItemClickListener { fileItem -> viewModel.openSelectedItem(fileItem) }
    }

    private fun initViewModels() {
        viewModel.isLoading.observe(this, Observer { isLoading ->
            fileManagerSwipeRefresh.isRefreshing = isLoading
        })
        viewModel.currentPath.observe(this, Observer { path ->
            fileManagerPath.text = path
        })
        viewModel.error.observe(this, Observer {
            Snackbar.make(fileManagerSwipeRefresh, getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
                .show()
        })
        viewModel.openFileIntent.observe(this, Observer { intentEvent ->
            intentEvent.getContentIfNotHandled()?.let {
                startActivity(it)
            }
        })
        viewModel.fileItemList.observe(this, Observer { fileList ->
            fileAdapter?.updateAppInfoList(fileList)
        })
    }

}
