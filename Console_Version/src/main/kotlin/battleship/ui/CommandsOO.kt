package battleship.ui

import battleship.model.*
import battleship.model.board.isComplete
import battleship.model.board.toDirection
import battleship.model.board.toPosition
import battleship.model.ship.toShipType
import battleship.model.ship.toShipTypeOrNull
import battleship.storage.Storage
import kotlinx.coroutines.runBlocking

private const val ERROR_INVALID_ARGUMENTS = "Invalid Arguments"

/**
 * Represents a command.
 */
abstract class CommandsOO {
    /**
     * Operation to be performed in the game with the indicated arguments.
     * @param game Actual game state.
     * @param args Arguments passed to command.
     * @return The game with the changes made or null if it is to end.
     */
    abstract fun action(game: Game, args: List<String>): Game?

    /**
     * Presentation of command result.
     * @param game Actual game state.
     */
    open fun show(game: Game) {}

    /**
     * Description of the syntax of the arguments
     */
    open val argsSyntax = ""
}

/**
 * Creates the associative map of game commands that associates the name of the command to its representation.
 */
fun getCommandsOO(st: Storage) = mapOf(

    "HELP" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>) = game
        override fun show(game: Game) {
            printHelp()
        }
    },
    "PUT" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game {
            require(args.size == 1 || args.size == 3) { ERROR_INVALID_ARGUMENTS }
            check(game.hasNotStarted) { "Can't change fleet after game started" }

            game as GameSetup

            if (args.size == 1) {
                return if (args[0] == "all") {
                    val res = game.putAllShips()
                    if (res.second === PutConsequence.INVALID_RANDOM)
                        error("Can't place more random ships")
                    res.first
                } else {
                    val type = args[0].toShipTypeOrNull() ?: error("Ship type ${args[0]} invalid")
                    val result = game.putRandomShip(type)
                    if (result.second === PutConsequence.INVALID_RANDOM) error("Can't place ${type.name} random ship")
                    if (result.second === PutConsequence.INVALID_SHIP) error("No more ${type.name} to put")
                    result.first
                }
            } else {
                val type = args[0].toShipType()
                val position = args[1].toPosition()
                val direction = args[2].toDirection()
                val result = game.putShip(type, position, direction)

                if (result.second === PutConsequence.INVALID_SHIP) error("No more ${type.name} to put")
                if (result.second === PutConsequence.INVALID_POSITION) error("Can't put ${type.name} in that position")

                return result.first
            }
        }

        override fun show(game: Game) {
            game.print()
        }

        override val argsSyntax: String
            get() = "(<shipType> [<position> <align>] | all)"
    },

    "REMOVE" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game {
            require(args.size == 1) { ERROR_INVALID_ARGUMENTS }
            check(game.hasNotStarted) { "Can't change fleet after game started" }

            game as GameSetup

            return if (args[0] == "all") {
                game.removeAll()
            } else {
                val position = args[0].toPosition()
                val updatedGame = game.removeShip(position)
                if (game.playerBoard === updatedGame.playerBoard) {
                    error("No ship in $position")
                }
                updatedGame
            }
        }

        override fun show(game: Game) {
            game.print()
        }

        override val argsSyntax: String
            get() = "<position> | all"
    },
    "GRID" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>) = game
        override fun show(game: Game) {
            game.print()

        }
    },
    "START" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game {
            require(args.size == 1 && args[0].isNotBlank()) { ERROR_INVALID_ARGUMENTS }
            check(game.hasNotStarted) { "Game Already Started" }
            check(game.playerBoard.fleet.isComplete()) { "Complete fleet before start" }
            val gameName = args[0]
            return runBlocking {
                (game as GameSetup).startGame(gameName, st)
            }
        }

        override fun show(game: Game) {
            game as GameFight
            println("You are the player ${game.player.name}")
        }

        override val argsSyntax: String
            get() = "<name>"
    },

    "SHOT" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game {
            require(args.size == 1) { ERROR_INVALID_ARGUMENTS }
            check(game.hasStarted) { "Can't make a shot before start" }
            return with(game as GameFight) {
                check(game.enemyBoard.fleet.isNotEmpty()) { "Wait for other player" }
                runBlocking {
                    val loadedGame = st.load(this@with)
                    val pos = args.first().toPosition()
                    val result = loadedGame.makeShot(pos, st, this)
                    check(result.second != ShotConsequence.INVALID) { "Position already used" }
                    printShotResult(result.second, result.third)
                    return@runBlocking result.first
                }
            }
        }

        override fun show(game: Game) {
            game.print()
        }

        override val argsSyntax = "<position>"
    },
    "REFRESH" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game {
            check(game.hasStarted) { "Can't refresh an open game" }
            return runBlocking {
                st.load(game as GameFight)
            }
        }

        override fun show(game: Game) {
            game.print()
        }
    },
    "EXIT" to object : CommandsOO() {
        override fun action(game: Game, args: List<String>): Game? = null
    }
)
