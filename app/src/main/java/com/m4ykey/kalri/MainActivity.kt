package com.m4ykey.kalri

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.m4ykey.kalri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var deviceManager: DeviceManager

    private var isEngineRunning = false

    private external fun toggleFilter(active : Boolean)
    private external fun setFilterParams(frequency : Float, dbGain : Float)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        deviceManager = DeviceManager(this) { message ->
            showToast(this, message)
        }
        deviceManager.startListening()

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            btnActivate.setOnClickListener {
                isEngineRunning = !isEngineRunning
                toggleFilter(isEngineRunning)

                btnActivate.text = if (isEngineRunning) "STOP ENGINE" else "START ENGINE"
            }

            sliderFreq.addOnChangeListener { _, value, _ ->
                if (isEngineRunning) {
                    setFilterParams(value, sliderGain.value)
                }
            }

            sliderGain.addOnChangeListener { _, value, _ ->
                if (isEngineRunning) {
                    setFilterParams(sliderFreq.value, value)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deviceManager.stopListening()
    }

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
}