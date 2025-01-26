package com.example.phocap.di

import com.example.phocap.ui.converter.CapsuleUiConverter
import com.example.phocap.utils.TimeHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ConverterModule {

    @Provides
    @Singleton
    fun provideConverter(timeHelper: TimeHelper) = CapsuleUiConverter(timeHelper)

}