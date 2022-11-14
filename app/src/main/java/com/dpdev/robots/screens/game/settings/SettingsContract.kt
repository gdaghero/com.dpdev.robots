package com.dpdev.robots.screens.game.settings

import com.dpdev.robots.mvi.UiAction
import com.dpdev.robots.mvi.UiEffect
import com.dpdev.robots.mvi.UiState

sealed interface SettingsContract {

    data class State(
        val players: Int = 0,
        val interval: Long = 0L,
        val rows: Int = 0,
        val columns: Int = 0,
        val maxPlayersCount: Int = 0
    ) : UiState

    sealed class Action : UiAction {
        object IncrementPlayerCount : Action()
        object DecrementPlayerCount : Action()
        object SaveSettings : Action()
    }

    sealed class Effect : UiEffect {
        object SettingsSaved : Effect()
    }
}
