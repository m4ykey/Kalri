package com.m4ykey.kalri

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.m4ykey.kalri.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel : MetronomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bpm.collect { bpm ->
                        binding.txtBpm.setText(bpm.toString())
                    }
                }

                launch {
                    viewModel.isRunning.collect { isRunning ->
                        binding.btnStart.text = if (isRunning) "Stop" else "Start"
                    }
                }

                launch {
                    viewModel.isSwitched.collect { isSwitched ->
                        if (binding.switchFilters.isChecked != isSwitched) {
                            binding.switchFilters.isChecked = isSwitched
                        }

                        binding.linearLayoutFilters.visibility = if (isSwitched) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            fabAdd.setOnClickListener {  }

            sliderBPM.addOnChangeListener { _, value, _ ->
                viewModel.setBpm(value.toInt())
            }

            btnAdd.setOnClickListener {
                viewModel.setBpm(viewModel.bpm.value + 1)
            }

            btnMinus.setOnClickListener {
                viewModel.setBpm(viewModel.bpm.value - 1)
            }

            btnStart.setOnClickListener {
                if (viewModel.isRunning.value) {
                    viewModel.stop()
                } else {
                    viewModel.start()
                }
            }

            sliderFreq.addOnChangeListener { _, value, _ ->
                viewModel.setFilterParams(value, sliderGain.value)
            }

            sliderGain.addOnChangeListener { _, value, _ ->
                viewModel.setFilterParams(sliderFreq.value, value)
            }

            switchFilters.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSwitched(isChecked)
            }
        }
    }

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
}