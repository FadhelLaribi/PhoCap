package com.example.phocap.di

import com.example.phocap.data.repository.CapsuleRepository
import com.example.phocap.data.repository.CapsuleRepositoryImpl
import com.example.phocap.data.repository.GroupRepository
import com.example.phocap.data.repository.GroupRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Singleton
    @Binds
    fun bindCapsuleRepository(impl: CapsuleRepositoryImpl): CapsuleRepository

}
