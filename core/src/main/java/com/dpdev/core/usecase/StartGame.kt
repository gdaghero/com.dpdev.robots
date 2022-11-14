package com.dpdev.core.usecase

import com.dpdev.core.repository.GameRepository
import javax.inject.Inject

class StartGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke() = gameRepository.startGame()
}
