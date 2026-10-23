package com.m4ykey.kalri

import android.app.Application
import com.m4ykey.kalri.di.kalriModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KalriApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@KalriApp)
            modules(kalriModule)
        }
    }

}