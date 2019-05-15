package com.example.systemious.ui.system_state

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.example.systemious.R
import com.example.systemious.domain.RamInfo
import kotlinx.android.synthetic.main.system_state_fragment.*

class SystemStateFragment : Fragment() {

    companion object {
        fun newInstance() = SystemStateFragment()
    }

    private lateinit var viewModel: SystemStateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.system_state_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SystemStateViewModel::class.java)
        viewModel.ramInfo.observe(this, Observer<RamInfo> { ramInfo ->
            systemStateTextView.text = "Total: " + ramInfo.totalRam + "\nUsed: " + ramInfo.usedRam
        })
        viewModel.coresNumber.observe(this, Observer<Int> { cores : Int -> coresTextView.text = cores.toString()})
        viewModel.minFrequencies.observe(this, Observer<IntArray> { minFreqs -> minFreqTextView.text =  printIntArray(minFreqs, "minFreqs") })
        viewModel.maxFrequencies.observe(this, Observer<IntArray> { maxFreqs -> maxFreqTextView.text = printIntArray(maxFreqs, "maxFreqs") })
        viewModel.currFrequencies.observe(this, Observer<IntArray> { curFreqs -> currFreqTextView.text = printIntArray(curFreqs, "curFreqs") })
    }

    private fun printIntArray(intArray: IntArray, description: String): String {
        var text = ""
        for (i in 0 until intArray.size) {
            text += "[$i] $description - ${intArray[i]}\n"
        }

        return text
    }

}
