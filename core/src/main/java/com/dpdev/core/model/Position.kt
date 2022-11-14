package com.dpdev.core.model

sealed class Position {

    abstract val coordinate: Coordinate

    data class Empty(
        override val coordinate: Coordinate
    ) : Position()

    data class Price(
        override val coordinate: Coordinate
    ) : Position()

    data class Taken(
        override val coordinate: Coordinate,
        val player: Player,
        val highlight: Boolean
    ) : Position()
}
