package com.dpdev.core.model

data class Game(
    val status: Status,
    val players: List<Player>,
    val currentRound: Round?,
    val points: List<Point>,
    val stalemateRounds: Int = 0
) {

    sealed class Status {
        object Idle : Status()
        object Started : Status()
        object Stopped : Status()
    }
}
