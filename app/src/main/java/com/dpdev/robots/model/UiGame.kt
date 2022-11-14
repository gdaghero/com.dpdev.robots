package com.dpdev.robots.model

import com.dpdev.core.model.Game

data class UiGame(
    val status: UiStatus,
    val players: List<UiPlayer>,
    val currentRound: UiRound?,
    val points: List<UiPoint>,
    val stalemateRounds: Int = 0
) {

    sealed class UiStatus {
        object Idle : UiStatus()
        object Started : UiStatus()
        object Stopped : UiStatus()
    }
}

fun Game.toUiGame() = UiGame(
    status = status.toUiStatus(),
    players = players.map { it.toUiPlayer() },
    currentRound = currentRound?.toUiRound(),
    points = points.map { it.toUiPoint() },
    stalemateRounds = stalemateRounds
)

fun Game.Status.toUiStatus() = when (this) {
    Game.Status.Idle -> UiGame.UiStatus.Idle
    Game.Status.Started -> UiGame.UiStatus.Started
    Game.Status.Stopped -> UiGame.UiStatus.Stopped
}
