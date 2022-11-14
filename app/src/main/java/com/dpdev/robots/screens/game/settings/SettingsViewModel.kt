package com.dpdev.robots.screens.game.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpdev.core.usecase.GetGameConfiguration
import com.dpdev.core.usecase.SaveGameConfiguration
import com.dpdev.robots.screens.game.settings.SettingsContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val saveGameConfiguration: SaveGameConfiguration,
    getGameConfiguration: GetGameConfiguration
) : ViewModel() {

    private val _uiState = MutableStateFlow(State())
    val uiState: StateFlow<State>
        get() = _uiState.asStateFlow()

    private val _uiActions = MutableSharedFlow<Action>()

    private val _uiEffects = Channel<Effect>()
    val uiEffects: Flow<Effect>
        get() = _uiEffects.receiveAsFlow()

    init {
        viewModelScope.launch { _uiActions.collect(::handleAction) }

        with(getGameConfiguration()) {
            _uiState.update {
                it.copy(
                    players = players,
                    rows = rows,
                    interval = turnDurationMillis,
                    columns = columns,
                    maxPlayersCount = maxPlayersCount
                )
            }
        }
    }

    fun sendAction(action: Action) {
        viewModelScope.launch { _uiActions.emit(action) }
    }

    private fun sendEffect(effect: Effect) {
        viewModelScope.launch { _uiEffects.send(effect) }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.SaveSettings -> saveSettings()
            is Action.IncrementPlayerCount -> incrementPlayerCount()
            is Action.DecrementPlayerCount -> decrementPlayerCount()
        }
    }

    private fun decrementPlayerCount() {
        _uiState.update {
            it.copy(
                players = if (it.players > 1) it.players - 1 else it.players
            )
        }
    }

    private fun incrementPlayerCount() {
        _uiState.update {
            it.copy(
                players = if (it.players < it.maxPlayersCount) it.players + 1 else it.players
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        _uiEffects.close()
    }

    private fun saveSettings() {
        saveGameConfiguration(players = _uiState.value.players)
        sendEffect(Effect.SettingsSaved)
    }
}
