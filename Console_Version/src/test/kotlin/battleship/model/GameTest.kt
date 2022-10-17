package battleship.model

import battleship.model.board.*
import battleship.model.ship.toShipType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue


class GameTest {
    @Test
    fun `check create game`() {
        val sut = createEmptyGame()
        assertTrue {
            sut.playerBoard.fleet.isEmpty() &&
            sut.playerBoard.grid.isEmpty()
        }
    }
}

