package com.example.test.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test.db.dao.*
import com.example.test.model.*

@Database(version = 1  , entities = [Post::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun postDao(): PostDao
}