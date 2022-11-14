package com.dpdev.core.repository

import com.dpdev.core.model.Board
import com.dpdev.core.model.Coordinate
import com.dpdev.core.model.Game
import com.dpdev.core.model.GameConfiguration
import com.dpdev.core.model.Player
import com.dpdev.core.model.Point
import com.dpdev.core.model.Position
import com.dpdev.core.model.Round
import com.dpdev.core.model.Turn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Named

interface GameRepository {

    val gameStream: Flow<Game?>
    val timeStream: Flow<Int>
    val gameConfiguration: GameConfiguration

    fun startGame(players: Int = gameConfiguration.players)
    fun stopGame()
    fun startRound()
    fun saveConfiguration(players: Int)
}

class DefaultGameRepository @Inject constructor(
    @Named("ioDispatcher") private val ioDispatcher: CoroutineDispatcher,
    private val configuration: GameConfiguration
) : GameRepository {

    private val lastCoordinates = mutableMapOf<Player, Coordinate>()
    private val playerPoints = mutableMapOf<Player, Int>()
    private val activePlayers = mutableMapOf<Player, Boolean>()
    private val remainingTurns = LinkedList<Turn>()
    private val currentPeriodTimeSeconds = AtomicInteger(0)
    private val coroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val jobs: MutableList<Job> = mutableListOf()
    private val _gameStream = MutableStateFlow<Game?>(null)
    private val _timerStream = MutableStateFlow(0)

    override val gameStream: Flow<Game?>
        get() = _gameStream.asStateFlow()

    override val timeStream: Flow<Int>
        get() = _timerStream.asStateFlow()

    override val gameConfiguration: GameConfiguration
        get() = configuration

    private val currentGame: Game
        get() = requireNotNull(_gameStream.value)

    private val currentRound: Round
        get() = requireNotNull(currentGame.currentRound)

    override fun startGame(players: Int) {
        val board = Board(rows = configuration.rows, columns = configuration.columns)
        val gamePlayers = createPlayers(players = players)

        _gameStream.update {
            Game(
                status = Game.Status.Idle,
                currentRound = Round(number = 1, board = board),
                players = gamePlayers,
                points = gamePlayers.map { playerPoints[it] = 0; Point(it, 0) }
            )
        }
        _timerStream.update { 0 }

        assignPlayers()
        assignPrice()
        assignTurns()
    }

    override fun stopGame() {
        _gameStream.update {
            it?.copy(
                status = Game.Status.Stopped,
                players = emptyList(),
                points = emptyList()
            )
        }
        jobs.forEach { it.cancel() }
        jobs.clear()
        playerPoints.clear()
        stopRound()
    }

    override fun startRound() {
        if (_gameStream.value?.status == Game.Status.Stopped)
            startGame()

        _gameStream.update { it?.copy(status = Game.Status.Started) }
        jobs += startTimer()
        jobs += startRobots()
    }

    override fun saveConfiguration(players: Int) {
        stopGame()
        startGame(players = players)
    }

    private fun stopRound() {
        remainingTurns.clear()
        lastCoordinates.clear()
        activePlayers.clear()
        remainingTurns.clear()

        _gameStream.update {
            it?.copy(
                currentRound = it.currentRound?.copy(
                    board = it.currentRound.board.also { board -> board.reset() }
                )
            )
        }
    }

    private fun restartRound() {
        stopRound()

        assignPlayers()
        assignPrice()
        assignTurns()

        _gameStream.update {
            it?.copy(
                currentRound = it.currentRound?.copy(
                    number = it.currentRound.number + 1
                )
            )
        }
    }

    private fun startTimer() = coroutineScope.launch {
        while (true) {
            delay(1000L)
            _timerStream.update { it + 1 }
            val seconds = currentPeriodTimeSeconds.updateAndGet { it + 1 }
            if (seconds >= configuration.turnDurationMillis / 1000L) {
                onTick()
                currentPeriodTimeSeconds.set(0)
            }
        }
    }

    private fun onTick() {
        if (remainingTurns.size > 0) {
            updateTurn(turn = remainingTurns.removeFirst())
            return
        }

        val isStalemate = activePlayers.none { (_, isActive) -> isActive }
        if (!isStalemate) {
            assignTurns()
            return
        }

        _gameStream.update {
            it?.copy(stalemateRounds = it.stalemateRounds + 1)
        }
        restartRound()
    }

    private fun startRobots() = currentGame.players.map {
        coroutineScope.launch {
            startRobot(player = it)
        }
    }

    private fun startRobot(player: Player) {
        while (true) tryMove(player)
    }

    private fun tryMove(currentPlayer: Player) {
        if (!canMove(player = currentPlayer)) return

        val currentCoordinate = requireNotNull(lastCoordinates[currentPlayer])
        val board = currentRound.board
        val coordinate = board
            .availableCoordinates(from = currentCoordinate)
            .shuffled()
            .firstOrNull { board.get(it) !is Position.Taken }

        if (coordinate == null) {
            // No more moves available for this player
            activePlayers[currentPlayer] = false
            return
        }

        val position = move(coordinate = coordinate)
        updateTurn()
        checkRoundStatus(player = currentPlayer, position = position)
    }

    private fun canMove(player: Player): Boolean = remainingTurns.size > 0 &&
        remainingTurns.peek()?.let { it.player == player && !it.hasMoved } ?: false

    private fun move(coordinate: Coordinate): Position {
        val turn = remainingTurns.peek()
        val board = currentRound.board
        val currentCoordinate = board.get(coordinate)
        val position = Position.Taken(
            coordinate = coordinate,
            player = turn.player,
            highlight = true
        )

        board.update(
            coordinate = lastCoordinates[turn.player]!!,
            position = position.copy(highlight = false)
        )
        board.set(coordinate = coordinate, position = position)
        lastCoordinates[turn.player] = coordinate
        turn.hasMoved = true

        return currentCoordinate
    }

    private fun checkRoundStatus(player: Player, position: Position) {
        if (position !is Position.Price) return

        playerPoints[player] = (playerPoints[player] ?: 0) + 1
        _gameStream.update {
            it?.copy(
                points = playerPoints.map { point ->
                    Point(player = point.key, points = point.value)
                }
            )
        }

        restartRound()
    }

    private fun updateTurn(turn: Turn = remainingTurns.peek()) {
        _gameStream.update {
            it?.copy(currentRound = currentRound.copy(turn = turn))
        }
    }

    private fun createPlayers(players: Int = configuration.players): List<Player> {
        val emojis = configuration.emojis.entries.shuffled().toMutableList()
        return (1..players).map {
            val emoji = emojis.removeFirst()
            Player(
                name = emoji.key,
                colorHex = String.format("#%06x", Random().nextInt(0xffffff + 1)),
                emoji = emoji.value
            )
        }
    }

    private fun assignPlayers(): List<Player> {
        val board = currentRound.board
        val assignedPlayers = mutableListOf<Player>()
        val remainingPlayers = currentGame.players
            .shuffled()
            .toMutableList()

        while (remainingPlayers.isNotEmpty()) {
            val player = remainingPlayers.removeFirst()
            var isAssigned = false

            while (!isAssigned) {
                val edgeCoordinate = board.edgeCoordinates()
                    .shuffled()
                    .first()

                if (board.get(edgeCoordinate) is Position.Empty) {
                    board.set(
                        coordinate = edgeCoordinate,
                        position = Position.Taken(
                            coordinate = edgeCoordinate,
                            player = player,
                            highlight = true
                        )
                    )
                    lastCoordinates[player] = edgeCoordinate
                    activePlayers[player] = true
                    assignedPlayers.add(player)
                    isAssigned = true
                }
            }
        }

        return assignedPlayers.shuffled()
    }

    private fun assignPrice() {
        val board = currentRound.board
        val availableCoordinates = board.availableCoordinates().shuffled()
        val coordinate = availableCoordinates.first()
        board.set(
            coordinate = coordinate,
            position = Position.Price(coordinate = coordinate)
        )
    }

    private fun assignTurns() {
        remainingTurns += currentGame.players
            .filter { activePlayers[it] == true }
            .map { player -> Turn(player = player, hasMoved = false) }
    }
}
