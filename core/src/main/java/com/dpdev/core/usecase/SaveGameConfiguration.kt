package com.dpdev.core.usecase

import com.dpdev.core.repository.GameRepository
import javax.inject.Inject

class SaveGameConfiguration @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(players: Int) {
        gameRepository.saveConfiguration(players)
    }
}
