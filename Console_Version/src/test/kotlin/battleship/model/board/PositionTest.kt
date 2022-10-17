package battleship.model.board

import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 * The f1.Position type identifies a position in the battleship game grid (Column and f1.Row)
 * Positions are identified by a letter and a number.
 * The upper left corner is "A1" and the lower right corner on a 10x10 grid is "J10".
 */
class PositionTest {
    @Test
    fun `Get position by index`() {
        val sut = Position.get(indexColumn = 2, indexRow = 3)
        assertEquals('C', sut.column.letter)
        assertEquals(4, sut.row.number)
        assertEquals("C4", sut.toString())
    }

    @Test
    fun `Get position by Column and Row`() {
        val sut = Position.Companion.get(2.indexToColumn(), 3.indexToRow())
        assertEquals("C4", sut.toString())
    }

    @Test
    fun `Get position using index operators`() {
        val p1 = Position[2, 3]
        val p2 = Position[2.indexToColumn(), 3.indexToRow()]
        assertEquals(p1.toString(), p2.toString())
        assertSame(p1, p2) // Identity
    }

    @Test
    fun `Position from String`() {
        val sut = "J10".toPositionOrNull()
        assertNotNull(sut)
        assertEquals("J10", sut.toString())
        assertEquals(null, "X3".toPositionOrNull())
        assertNotNull("A1".toPositionOrNull())
        assertSame(Position[0, 0], "A1".toPosition())
        assertFailsWith<IllegalStateException> { "$12".toPosition() }
    }

    @Test
    fun `Test Move Position`() {
        // move 0
        val origin = Position[0, 0]
        assertSame(origin, origin.movePosition(Direction.HORIZONTAL, 0))
        assertSame(origin, origin.movePosition(Direction.VERTICAL, 0))
        // move length
        assertSame(Position[5, 0], origin.movePosition(Direction.HORIZONTAL, 5))
        assertSame(Position[0, 5], origin.movePosition(Direction.VERTICAL, 5))
    }

    @Test
    fun `Test Move Position OutOfBounds`() {
        // move 0
        val origin = Position[0, 0]

        assertThrows<IndexOutOfBoundsException> { origin.movePosition(Direction.HORIZONTAL, COLUMN_DIM + 1) }
        assertThrows<IndexOutOfBoundsException> { origin.movePosition(Direction.VERTICAL, ROW_DIM + 1) }
    }
}
