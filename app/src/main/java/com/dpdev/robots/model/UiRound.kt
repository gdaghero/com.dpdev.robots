package com.dpdev.robots.model

import com.dpdev.core.model.Round

data class UiRound(
    val number: Int,
    val board: UiBoard,
    val turn: UiTurn? = null
)

fun Round.toUiRound() = UiRound(
    number = number,
    board = board.toUiBoard(),
    turn = turn?.toUiTurn()
)
