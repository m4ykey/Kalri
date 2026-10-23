package com.m4ykey.kalri.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    version = 1,
    entities = [MetronomeEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao() : MetronomeDao
}