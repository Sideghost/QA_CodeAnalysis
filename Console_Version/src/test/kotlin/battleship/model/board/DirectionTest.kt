import battleship.model.board.Direction
import battleship.model.board.toDirection
import battleship.model.board.toDirectionOrNull
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertSame

class DirectionTest {

    @Test
    fun `String to Direction`() {
        assertSame(Direction.HORIZONTAL, "h".toDirectionOrNull())
        assertSame(Direction.VERTICAL, "v".toDirectionOrNull())
        assertSame(null, "k".toDirectionOrNull())
    }

    @Test
    fun `String to Direction Exceptions`() {
        assertThrows<IllegalStateException> { "dfhdfherhtrh".toDirection() }
    }
}
