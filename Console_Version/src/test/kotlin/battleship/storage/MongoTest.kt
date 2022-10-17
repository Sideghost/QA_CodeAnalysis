package battleship.storage

import battleship.model.Game
import battleship.model.board.Board
import mongoDB.MongoDriver
import mongoDB.getDocument
import mongoDB.insertDocument
import mongoDB.replaceDocument
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MongoTest {
    data class Doc(val _id: String, val field: Int)

    data class GameDoc(val _id: String, val boardA: Board, val boardB: Board)

    private val docName = "testing"

    @Test
    fun `test insert Document`() = MongoDriver("PVV").use {

    }

    @Test
    fun `testing unchanged game`() = MongoDriver("Game_test").use {

    }
}
