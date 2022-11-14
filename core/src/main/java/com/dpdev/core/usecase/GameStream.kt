package com.dpdev.core.usecase

import com.dpdev.core.model.Game
import com.dpdev.core.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameStream @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(): Flow<Game?> = gameRepository.gameStream
}
