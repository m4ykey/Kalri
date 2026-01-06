package com.m4ykey.kalri

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager

class DeviceManager(
    context : Context,
    private val onDeviceChanged : (String) -> Unit
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val deviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo?>?) {
            checkAudioDevice()
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo?>?) {
            onDeviceChanged("Disconnected")
        }
    }

    fun startListening() {
        audioManager.registerAudioDeviceCallback(deviceCallback, null)
        checkAudioDevice()
    }

    fun stopListening() {
        audioManager.unregisterAudioDeviceCallback(deviceCallback)
    }

    fun checkAudioDevice() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            val name = when (device.type) {
                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_USB_ACCESSORY -> "Detected: ${device.productName}"

                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Detected Wired Headphones"

                else -> null
            }
            name?.let { onDeviceChanged(it) }
        }
    }
}