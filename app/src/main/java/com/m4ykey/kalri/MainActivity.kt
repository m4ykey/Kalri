package com.m4ykey.kalri

import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.m4ykey.kalri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var audioManager : AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        checkAudioDevice()

        audioManager.registerAudioDeviceCallback(object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo?>?) {
                checkAudioDevice()
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo?>?) {
                showToast(this@MainActivity, "Disconnected")
            }
        }, null)
        
        binding.sampleText.text = stringFromJNI()
    }
    
    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
    
    private fun checkAudioDevice() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        for (device in devices) {
            when (device.type) {
                AudioDeviceInfo.TYPE_USB_DEVICE,
                AudioDeviceInfo.TYPE_USB_HEADSET,
                AudioDeviceInfo.TYPE_USB_ACCESSORY -> {
                    showToast(this, "Detected: ${device.productName}")
                }

                AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> {
                    showToast(this, "Detected Wired Headphones")
                }
            }
        }
    }
}