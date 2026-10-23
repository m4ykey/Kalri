package com.m4ykey.kalri.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MetronomeDao {

    @Query("SELECT * FROM metronome_table ORDER BY id ASC")
    fun getAll() : Flow<List<MetronomeEntity>>

    @Insert
    suspend fun insert(item : MetronomeEntity)

    @Delete
    suspend fun delete(item : MetronomeEntity)

}