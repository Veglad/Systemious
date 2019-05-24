package com.example.systemious.ui.file_manager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.systemious.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.file_manager_fragment.*

class FileManager : Fragment() {

    companion object {
        fun newInstance() = FileManager()
        private const val MEMORY_PERMISSION_REQUEST_CODE = 1523
    }

    private lateinit var viewModel: FileManagerViewModel
    private var fileAdapter: FileManagerRecyclerAdapter? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_manager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        snackbar?.dismiss()
        viewModel = ViewModelProviders.of(this).get(FileManagerViewModel::class.java)
        fileManagerSwipeRefresh.setOnRefreshListener { loadFiles() }
        initRecyclerView()
        initViewModels()
    }

    private fun loadFiles() {
        if (isStoragePermissionGranted()) {
            viewModel.loadFiles()
        } else {
            fileManagerSwipeRefresh.isRefreshing = false
            requestPermission()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), MEMORY_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        val activity = activity as Activity
        when (requestCode) {
            MEMORY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.loadFiles()
                } else {
                    val isNeverAskAgainChecked =
                        !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)

                    snackbar = Snackbar.make(
                        fileManagerFrameLayout,
                        activity.getString(R.string.memory_permission_disabled),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.grant_permission) {
                            grantPermissionPressed(isNeverAskAgainChecked)
                        }
                    snackbar?.show()
                }
                return
            }
            else -> {
            }
        }
    }

    private fun grantPermissionPressed(isNeverAskAgainChecked: Boolean) {
        val activity = activity as Activity
        if (isNeverAskAgainChecked) {
            startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.fromParts("package", activity.packageName, null)
                startActivity(this)
            })
            closeApp(activity)
        } else {
            requestPermission()
        }
    }

    private fun closeApp(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAndRemoveTask()
        } else {
            activity.finishAffinity()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val isWritePermissionGrandted = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) === PackageManager.PERMISSION_GRANTED
            val isReadPermissionGrandted = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) === PackageManager.PERMISSION_GRANTED
            !(!isWritePermissionGrandted || !isReadPermissionGrandted)
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
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
        viewModel.grantPermission.observe(this, Observer { event ->
            event.performEventIfNotHandled {
                requestPermission()
            }
        })
    }
}
