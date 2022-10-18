package storage

import battleship.model.board.Board
import mongoDB.MongoDriver
import org.junit.jupiter.api.Test


class MongoTest {
    private val dbName = "qa-erasmus"

    data class Doc(val _id: String, val field: Int)

    data class GameDoc(val _id: String, val boardA: Board, val boardB: Board)

    private val docName = "testing"

    @Test
    fun `test insert Document`() = MongoDriver(dbName).use {

    }

    @Test
    fun `testing unchanged game`() = MongoDriver(dbName).use {

    }
}
