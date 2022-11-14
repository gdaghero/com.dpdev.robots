package com.dpdev.core.usecase

import com.dpdev.core.repository.GameRepository
import javax.inject.Inject

class StopGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke() {
        gameRepository.stopGame()
    }
}
