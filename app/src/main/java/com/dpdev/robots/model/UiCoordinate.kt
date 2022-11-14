package com.dpdev.robots.model

import com.dpdev.core.model.Coordinate

data class UiCoordinate(
    val x: Int,
    val y: Int
)

fun Coordinate.toUiCoordinate() = UiCoordinate(
    x = x,
    y = y
)
