package com.example.phocap.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.phocap.data.model.entity.CapsuleEntity
import com.example.phocap.data.model.entity.GroupEntity
import com.example.phocap.data.model.entity.UriEntity

@Database(
    entities = [CapsuleEntity::class, UriEntity::class, GroupEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun capsuleDao(): CapsuleDao
}
