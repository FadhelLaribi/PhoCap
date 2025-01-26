package com.example.phocap.di

import com.example.phocap.utils.TimeHelper
import com.example.phocap.utils.TimeHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface HelperModule {

    @Singleton
    @Binds
    fun bindTimeHelper(impl: TimeHelperImpl): TimeHelper

}