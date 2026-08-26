package com.m4ykey.kalri

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.m4ykey.kalri.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel : MetronomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bpm.collect { bpm ->
                        binding.txtBpm.text = "$bpm BPM"
                    }
                }

                launch {
                    viewModel.isRunning.collect { isRunning ->
                        binding.btnStart.text = if (isRunning) "Stop" else "Start"
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
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
        }
    }

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
}