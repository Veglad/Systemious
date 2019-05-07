package com.example.systemious.ui.file_manager

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.systemious.R

class FileManager : Fragment() {

    companion object {
        fun newInstance() = FileManager()
    }

    private lateinit var viewModel: FileManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FileManagerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
