package com.m4ykey.kalri

class MetronomeController {

    private external fun nativeToggleFilter(active : Boolean)
    private external fun nativeSetBpm(bpm : Int)
    private external fun nativeSetFilterParams(frequency: Float, dbGain: Float)

    fun setFilterParams(frequency: Float, dbGain: Float) {
        nativeSetFilterParams(frequency, dbGain)
    }

    fun start() {
        nativeToggleFilter(true)
    }

    fun stop() {
        nativeToggleFilter(false)
    }

    fun setBpm(bpm: Int) {
        nativeSetBpm(bpm)
    }

}