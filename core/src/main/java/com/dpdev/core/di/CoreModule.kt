package com.dpdev.core.di

import com.dpdev.core.model.GameConfiguration
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Named("ioDispatcher")
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideGameConfiguration(): GameConfiguration = GameConfiguration()
}
