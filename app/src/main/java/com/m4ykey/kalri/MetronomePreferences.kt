package com.m4ykey.kalri

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MetronomePreferences(
    private val dataStore : DataStore<Preferences>
) {

    companion object {
        private val SELECTED_SWITCH_STATE = booleanPreferencesKey("selected_switch")
    }

    suspend fun saveSwitchState(isSwitched : Boolean) {
        runCatching {
            dataStore.edit { pref ->
                pref[SELECTED_SWITCH_STATE] = isSwitched
            }
        }.onFailure { exception ->
            if (exception is IOException) {
                exception.printStackTrace()
            }
        }.getOrNull()
    }

    fun getSwitchState() : Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { pref ->
                pref[SELECTED_SWITCH_STATE] ?: true
            }
    }
}