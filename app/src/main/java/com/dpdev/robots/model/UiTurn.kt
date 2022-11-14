package com.dpdev.robots.model

import com.dpdev.core.model.Turn

data class UiTurn(
    val player: UiPlayer,
    val hasMoved: Boolean
)

fun Turn.toUiTurn() = UiTurn(
    player = player.toUiPlayer(),
    hasMoved = hasMoved
)
