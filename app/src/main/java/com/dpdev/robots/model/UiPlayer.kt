package com.dpdev.robots.model

import com.dpdev.core.model.Player

data class UiPlayer(
    val name: String,
    val colorHex: String,
    val emoji: String
)

fun Player.toUiPlayer() = UiPlayer(
    name = name,
    colorHex = colorHex,
    emoji = emoji
)
