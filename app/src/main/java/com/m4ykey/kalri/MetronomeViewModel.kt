package com.m4ykey.kalri

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MetronomeViewModel : ViewModel() {

    private val controller = MetronomeController()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _bpm = MutableStateFlow(40)
    val bpm = _bpm.asStateFlow()

    private val _frequency = MutableStateFlow(440f)
    val frequency = _frequency.asStateFlow()

    private val _gain = MutableStateFlow(0f)
    val gain = _gain.asStateFlow()

    fun setFilterParams(freq: Float, gain : Float) {
        controller.setFilterParams(freq, gain)

        _frequency.value = freq
        _gain.value = gain
    }

    fun start() {
        controller.start()
        _isRunning.value = true
    }

    fun stop() {
        controller.stop()
        _isRunning.value = false
    }

    fun setBpm(bpm : Int) {
        controller.setBpm(bpm)
        _bpm.value = bpm
    }

}