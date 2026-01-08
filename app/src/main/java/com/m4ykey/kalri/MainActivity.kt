package com.m4ykey.kalri

import android.os.Bundle
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.m4ykey.kalri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isMetronomeRunning = false

    private external fun toggleFilter(active : Boolean)
    private external fun setFilterParams(frequency : Float, dbGain : Float)
    private external fun setBPM(bpm : Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialBpm = binding.sliderBPM.value.toInt()
        binding.txtBpm.text = "$initialBpm BPM"

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            sliderBPM.addOnChangeListener { _, value, _ ->
                val bpm = value.toInt()
                txtBpm.text = "$bpm BPM"
                setBPM(bpm)
            }

            btnAdd.setOnClickListener {
                val newValue = sliderBPM.value + 1f
                if (newValue <= sliderBPM.valueTo) sliderBPM.value = newValue
            }

            btnMinus.setOnClickListener {
                val newValue = sliderBPM.value - 1f
                if (newValue >= sliderBPM.valueFrom) sliderBPM.value = newValue
            }

            btnStart.setOnClickListener {
                isMetronomeRunning = !isMetronomeRunning
                toggleFilter(isMetronomeRunning)

                if (isMetronomeRunning) {
                    setBPM(sliderBPM.value.toInt())
                    setFilterParams(sliderFreq.value, sliderGain.value)
                }

                btnStart.text = if (isMetronomeRunning) "Stop" else "Start"
            }

            sliderFreq.addOnChangeListener { _, value, _ ->
                if (isMetronomeRunning) {
                    setFilterParams(value, sliderGain.value)
                }
            }

            sliderGain.addOnChangeListener { _, value, _ ->
                if (isMetronomeRunning) {
                    setFilterParams(sliderFreq.value, value)
                }
            }
        }
    }

    @Keep
    fun triggerBeat() {
        runOnUiThread {
            binding.txtBpm.alpha = 0.5f
            binding.txtBpm.animate().alpha(1.0f).setDuration(100).start()
        }
    }

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
}