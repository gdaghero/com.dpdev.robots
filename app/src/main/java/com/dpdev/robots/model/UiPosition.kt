package com.dpdev.robots.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.dpdev.core.model.Position
import com.dpdev.robots.R

sealed class UiPosition {

    open val colorRes: Int = -1
    abstract val coordinate: UiCoordinate

    data class Price(
        override val coordinate: UiCoordinate,
        @ColorRes override val colorRes: Int = R.color.board_position_price_background
    ) : UiPosition()

    data class Taken(
        override val coordinate: UiCoordinate,
        val highlight: Boolean,
        val player: UiPlayer
    ) : UiPosition()

    data class Empty(
        override val coordinate: UiCoordinate,
        @ColorRes override val colorRes: Int = R.color.board_position_untaken_background
    ) : UiPosition()
}

fun Position.toUiPosition() = when (this) {
    is Position.Empty -> UiPosition.Empty(coordinate = coordinate.toUiCoordinate())
    is Position.Price -> UiPosition.Price(coordinate = coordinate.toUiCoordinate())
    is Position.Taken -> UiPosition.Taken(
        highlight = highlight,
        coordinate = coordinate.toUiCoordinate(),
        player = player.toUiPlayer()
    )
}
