package com.m4ykey.kalri.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "metronome_table")
data class MetronomeEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    val bpm : Int
)
