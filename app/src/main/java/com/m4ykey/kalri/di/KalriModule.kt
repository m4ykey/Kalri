package com.m4ykey.kalri.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.m4ykey.kalri.data.local.AppDatabase
import com.m4ykey.kalri.data.preferences.MetronomePreferences
import com.m4ykey.kalri.data.repository.MetronomeRepository
import com.m4ykey.kalri.data.repository.MetronomeRepositoryImpl
import com.m4ykey.kalri.metronome.MetronomeViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val Context.appDataStore by preferencesDataStore(name = "kalri_pref")

val kalriModule = module {
    viewModelOf(::MetronomeViewModel)

    singleOf(::MetronomePreferences)

    single<DataStore<Preferences>> { androidContext().appDataStore }

    singleOf(::MetronomeRepositoryImpl) bind MetronomeRepository::class

    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            name = "app_database",
            context = androidContext()
        )
            .setQueryCoroutineContext(Dispatchers.IO)
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    single { get<AppDatabase>().dao() }
}