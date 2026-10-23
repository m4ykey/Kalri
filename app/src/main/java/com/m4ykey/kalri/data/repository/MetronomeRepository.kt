package com.m4ykey.kalri.data.repository

import com.m4ykey.kalri.data.local.MetronomeEntity
import kotlinx.coroutines.flow.Flow

interface MetronomeRepository {

    fun getAll() : Flow<List<MetronomeEntity>>
    suspend fun insert(item : MetronomeEntity)
    suspend fun delete(item : MetronomeEntity)

}