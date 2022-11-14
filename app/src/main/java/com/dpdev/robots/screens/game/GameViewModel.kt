package com.dpdev.robots.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dpdev.core.usecase.GameStream
import com.dpdev.core.usecase.StartGame
import com.dpdev.core.usecase.StartRound
import com.dpdev.core.usecase.StopGame
import com.dpdev.core.usecase.TimeStream
import com.dpdev.robots.model.UiGame
import com.dpdev.robots.model.UiScore
import com.dpdev.robots.model.toUiGame
import com.dpdev.robots.screens.game.GameUiContract.Action
import com.dpdev.robots.screens.game.GameUiContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val startRound: StartRound,
    private val gameStream: GameStream,
    private val timeStream: TimeStream,
    private val stopGame: StopGame,
    startGame: StartGame
) : ViewModel() {

    private val _uiActions = MutableSharedFlow<Action>()
    private val _uiState = MutableStateFlow(State())
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiActions.collect(::handleAction)
        }

        viewModelScope.launch {
            gameStream()
                .stateIn(this)
                .combine(timeStream()) { game, time -> game to time }
                .collectLatest { (game, time) ->
                    _uiState.update {
                        it.copy(game = game?.toUiGame(), elapsedTimeSeconds = time)
                    }
                    updateScore()
                }
        }

        startGame()
    }

    fun sendAction(action: Action) {
        viewModelScope.launch { _uiActions.emit(action) }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.ToggleGame -> toggleGame()
        }
    }

    private fun toggleGame() {
        val isPlaying = _uiState.value.game?.status is UiGame.UiStatus.Started
        viewModelScope.launch {
            runCatching {
                if (isPlaying) stopGame() else startRound()
            }
                .onFailure { Log.e(TAG, "startRound", it) }
        }
    }

    private fun updateScore() {
        val game = _uiState.value.game ?: return
        _uiState.update {
            it.copy(
                score = game.points
                    .map { point ->
                        UiScore(
                            point = point,
                            isPlaying = point.player == game.currentRound?.turn?.player
                        )
                    }
                    .sortedByDescending { score -> score.point.points }
            )
        }
    }

    override fun onCleared() {
        stopGame()
        super.onCleared()
    }

    companion object {
        private const val TAG = "GameViewModel"
    }
}
