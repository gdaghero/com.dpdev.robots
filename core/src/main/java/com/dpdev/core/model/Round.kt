package com.dpdev.core.model

data class Round(
    val number: Int,
    val board: Board,
    val turn: Turn? = null
)
