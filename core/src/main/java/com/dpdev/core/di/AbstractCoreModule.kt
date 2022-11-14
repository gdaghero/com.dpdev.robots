package com.dpdev.core.di

import com.dpdev.core.repository.GameRepository
import com.dpdev.core.repository.DefaultGameRepository
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@dagger.Module
@InstallIn(SingletonComponent::class)
abstract class AbstractCoreModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: DefaultGameRepository
    ): GameRepository
}
