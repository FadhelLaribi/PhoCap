package com.example.phocap.di

import android.content.Context
import androidx.room.Room
import com.example.phocap.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "phocap")
            .fallbackToDestructiveMigration() // In a real app migrations should be used
            .build()

    @Provides
    fun provideGroupDao(database: AppDatabase) = database.groupDao()

    @Provides
    fun provideCapsuleDao(database: AppDatabase) = database.capsuleDao()
}