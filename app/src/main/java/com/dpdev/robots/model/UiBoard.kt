package com.dpdev.robots.model

import com.dpdev.core.model.Board

data class UiBoard(
    val rows: Int,
    val columns: Int,
    val positions: List<UiPosition>
)

fun Board.toUiBoard() = UiBoard(
    rows = rows,
    columns = columns,
    positions = positions.map { it.toUiPosition() }
)
