package com.dpdev.core.model

class Board(val rows: Int, val columns: Int) {

    private val _map = mutableMapOf<Coordinate, Position>()
    val positions: List<Position>
        get() = _map.values.toList()

    init {
        reset()
    }

    fun reset() {
        for (row in (0 until rows)) {
            for (column in (0 until columns)) {
                val coordinate = Coordinate(row, column)
                _map[coordinate] = Position.Empty(coordinate = coordinate)
            }
        }
    }

    fun edgeCoordinates(): List<Coordinate> = listOf(
        Coordinate(0, 0),
        Coordinate(0, columns - 1),
        Coordinate(rows - 1, columns - 1),
        Coordinate(rows - 1, 0)
    )

    fun availableCoordinates(): List<Coordinate> =
        _map.entries
            .filter { it.value !is Position.Taken }
            .map { it.key }

    fun availableCoordinates(from: Coordinate): List<Coordinate> {
        val (x, y) = from
        return Direction.values()
            .map { direction ->
                when (direction) {
                    Direction.LEFT -> Coordinate(x = x, y = y - 1)
                    Direction.RIGHT -> Coordinate(x = x, y = y + 1)
                    Direction.UP -> Coordinate(x = x - 1, y = y)
                    Direction.DOWN -> Coordinate(x = x + 1, y = y)
                }
            }
            .filter { it.isInRange() }
    }

    private fun Coordinate.isInRange(): Boolean {
        val (x, y) = this
        return x in 0 until rows && y in (0 until columns)
    }

    fun set(coordinate: Coordinate, position: Position) {
        _map[coordinate] = position
    }

    fun get(coordinate: Coordinate): Position =
        requireNotNull(_map[coordinate])

    fun update(coordinate: Coordinate, position: Position) {
        _map[coordinate] = position
    }

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
