package com.dpdev.core.usecase

import com.dpdev.core.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeStream @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(): Flow<Int> = gameRepository.timeStream
}
