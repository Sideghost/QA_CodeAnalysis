package battleship.model

import org.junit.jupiter.api.Test
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

