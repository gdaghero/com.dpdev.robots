package com.dpdev.core.model

data class GameConfiguration(
    val players: Int = 2,
    val rows: Int = 7,
    val columns: Int = 7,
    val turnDurationMillis: Long = 500L,
    val maxPlayersCount: Int = 4,
    val emojis: Map<String, String> = mapOf(
        "GRINNING FACE" to "\uD83D\uDE00",
        "SMILING FACE WITH HEART-SHAPED EYES" to "\uD83D\uDE0D",
        "FACE WITH OPEN MOUTH VOMITING" to "\uD83E\uDD2E",
        "SMILING FACE WITH HORNS" to "\uD83D\uDE08",
        "SKULL" to "\uD83D\uDC80",
        "COWBOY HAT FACE" to "\uD83E\uDD20",
        "JAPANESE OGRE" to "\uD83D\uDC79",
        "GHOST" to "\uD83D\uDC7B",
        "PARTYING FACE" to "\uD83E\uDD73",
        "SOCCER BALL" to "⚽︎",
        "HEART" to "❤️",
        "BILLIARDS" to "\uD83C\uDFB1",
        "VIDEO GAME" to "\uD83C\uDFAE",
        "BLACK HEART" to "\uD83D\uDDA4"
    )
)
