package com.m4ykey.kalri.data.repository

import com.m4ykey.kalri.data.local.MetronomeDao
import com.m4ykey.kalri.data.local.MetronomeEntity
import kotlinx.coroutines.flow.Flow

class MetronomeRepositoryImpl(private val dao : MetronomeDao) : MetronomeRepository {

    override fun getAll(): Flow<List<MetronomeEntity>> {
        return dao.getAll()
    }

    override suspend fun insert(item: MetronomeEntity) {
        return dao.insert(item)
    }

    override suspend fun delete(item: MetronomeEntity) {
        return dao.delete(item)
    }
}