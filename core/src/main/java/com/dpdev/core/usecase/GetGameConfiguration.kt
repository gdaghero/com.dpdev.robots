package com.dpdev.core.usecase

import com.dpdev.core.model.GameConfiguration
import com.dpdev.core.repository.GameRepository
import javax.inject.Inject

class GetGameConfiguration @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(): GameConfiguration =
        gameRepository.gameConfiguration
}
