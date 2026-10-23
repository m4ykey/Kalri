package com.m4ykey.kalri.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.m4ykey.kalri.data.preferences.MetronomePreferences
import com.m4ykey.kalri.metronome.MetronomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val Context.appDataStore by preferencesDataStore(name = "kalri_pref")

val kalriModule = module {
    viewModelOf(::MetronomeViewModel)

    singleOf(::MetronomePreferences)

    single<DataStore<Preferences>> { androidContext().appDataStore }
}