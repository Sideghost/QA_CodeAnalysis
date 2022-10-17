package battleship.storage

import battleship.model.Game
import battleship.model.GameFight
import battleship.model.Player
import battleship.model.board.*
import battleship.model.chooseUponPlayer
import battleship.model.ship.Ship
import battleship.model.ship.toShipType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mongoDB.*

private const val SHIP_INDICATOR = "S"
private const val MISS_INDICATOR = "M"
private const val SHIP_CELL_SHOT = '1'
private const val SHIP_CELL = '0'

class MongoStorage(driver: MongoDriver) : Storage {


    /**
     * Representation of the game state in a document.
     */
    private data class Doc(val _id: String, val contentA: List<String>, val contentB: List<String>, val turn: String)

    /**
     * The collection with all the games.
     */
    private val collection = driver.getCollection<Doc>("games")

    /**
     * Function that will serialize a [Ship] into a string
     * @param grid [Grid] grid where the ship is located
     * @return string information in string
     */
    private fun Ship.serialize(grid: Grid): String {
        val name = type.name
        val cellStatus = positions.map { if (grid[it] is ShipHit) SHIP_CELL_SHOT else SHIP_CELL }.joinToString("")
        return ("$SHIP_INDICATOR $name $head $dir $cellStatus")
    }

    /**
     * Function that will serialize a [Board] into a [FileContent]
     * @return [FileContent] with all the content that will be uploaded into the [Storage]
     */
    private fun Board.serialize(): FileContent {
        val fleetEntries = fleet.map { it.serialize(grid) }
        val missEntries = grid.entries.mapNotNull { if (it.value is MissCell) "$MISS_INDICATOR ${it.key}" else null }
        return fleetEntries + missEntries
    }

    /**
     * Function that will deserialize a string from a file
     * @return pair with reference to the ship and the list of Cells from the ship
     */
    private fun String.deserialize(): Pair<Ship?, List<Cell>> {
        val split = split(" ")
        if (split.first() == SHIP_INDICATOR) {
            val type = split[1].toShipType()
            val head = split[2].toPosition()
            val dir = split[3].toDirection()
            val hits = split[4]
            val shipPositions = getPositionsFromLine(head, dir, type.squares)
            val shipField = getField(head, dir, type.squares)
            val ship = Ship(type, head, dir, shipPositions, shipField)

            val shipCells = if (hits.all { it == SHIP_CELL_SHOT })
                shipPositions.map { ShipSunk(it, ship) }
            else
                shipPositions.mapIndexed { num, it ->
                    if (hits[num] == SHIP_CELL_SHOT) ShipHit(it, ship) else ShipCell(
                        it,
                        ship
                    )
                }

            return ship to shipCells

        } else if (split.first() == MISS_INDICATOR) {
            val pos = split[1].toPosition()
            val newCell = MissCell(pos)
            return null to listOf(newCell)
        }
        return null to emptyList()
    }

    /**
     * Function that will deserialize the file into a [Board]
     * @return [Board]
     */
    private fun FileContent.deserialize(): Board {
        val entries = map { it.deserialize() }
        val ships = entries.mapNotNull { it.first }
        val grid = entries.map { it.second }.flatten().associateBy { it.pos }

        return Board(ships, grid)
    }

    /**
     * Function that will start a game making the needed changes to the DB
     * @param name name of the game
     * @param board board of the player to start the game with
     * @return [Player] that started the game
     */
    override suspend fun start(name: String, board: Board): Player =
        withContext(Dispatchers.IO) {
            val doc = collection.getDocument(name)
            if (doc != null) {
                if (doc.contentA.isNotEmpty() && doc.contentB.isEmpty()) {
                    collection.replaceDocument(Doc(name, doc.contentA, board.serialize(), doc.turn))
                    return@withContext Player.B
                } else {
                    collection.deleteDocument(name)
                }
            }
            val boardAEntry = board.serialize()
            collection.insertDocument(Doc(name, boardAEntry, emptyList(), Player.A.name))
            return@withContext Player.A
        }

    /**
     * Function that will store a game in the DB making the needed changes.
     * @param game [Game] to store.
     */
    override suspend fun store(game: GameFight) {
        withContext(Dispatchers.IO) {
            val boardAEntry = chooseUponPlayer(game.player, game.playerBoard, game.enemyBoard).serialize()
            val boardBEntry = chooseUponPlayer(game.player.other(), game.playerBoard, game.enemyBoard).serialize()
            collection.replaceDocument(Doc(game.name, boardAEntry, boardBEntry, game.turn.name))
        }
    }

    /**
     * Function that will load a game from the DB.
     * @param game [Game] to load.
     *
     */
    override suspend fun load(game: GameFight): GameFight =
        withContext(Dispatchers.IO) {
            val doc = collection.getDocument(game.name)
            checkNotNull(doc) { "No document in Load" }
            val boardA = doc.contentA.deserialize()
            val boardB = if (doc.contentB.isNotEmpty()) doc.contentB.deserialize() else Board()
            val playerBoard = chooseUponPlayer(game.player, boardA, boardB)
            val enemyBoard = chooseUponPlayer(game.player.other(), boardA, boardB)
            return@withContext GameFight(
                playerBoard,
                enemyBoard,
                game.name,
                player = game.player,
                turn = Player.valueOf(doc.turn)
            )
        }
}
