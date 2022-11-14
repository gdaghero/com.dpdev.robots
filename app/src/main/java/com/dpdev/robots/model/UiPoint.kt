package com.dpdev.robots.model

import com.dpdev.core.model.Point

data class UiPoint(
    val player: UiPlayer,
    val points: Int
)

fun Point.toUiPoint() = UiPoint(
    player = player.toUiPlayer(),
    points = points
)
