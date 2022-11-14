package com.dpdev.robots.screens.game

import com.dpdev.robots.model.UiGame
import com.dpdev.robots.model.UiScore
import com.dpdev.robots.mvi.UiAction
import com.dpdev.robots.mvi.UiState

interface GameUiContract {

    data class State(
        val game: UiGame? = null,
        val elapsedTimeSeconds: Int = 0,
        val score: List<UiScore> = emptyList()
    ) : UiState

    sealed class Action : UiAction {
        object ToggleGame : Action()
    }
}
