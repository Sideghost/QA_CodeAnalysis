package battleship.model.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * The [Row] type identifies one of the game's grid lines.
 * Rows are identified by a number from 1 to 10 if the grid has 10 rows.
 * The top row is number 1.
 */
class RowTest {
    @Test
    fun `number of rows`() {
        assertEquals(10, ROW_DIM)
        assertEquals(ROW_DIM, Row.values.size)
    }

    @Test
    fun `number of each row`() {
        assertEquals(1, Row.values.first().number)
        assertEquals(10, Row.values.last().number)
        assertEquals((1..ROW_DIM).toList(), Row.values.map { it.number })
    }

    @Test
    fun `ordinal of rows`() {
        assertEquals(0, Row.values.first().ordinal)
        assertEquals(ROW_DIM - 1, Row.values.last().ordinal)
        assertEquals((0 until ROW_DIM).toList(), Row.values.map { it.ordinal })
    }

    @Test
    fun `row from number`() {
        assertSame(1.toRowOrNull(), Row.values[0])
        assertSame(10.toRowOrNull(), Row.values[ROW_DIM - 1])
        assertEquals(null, 99.toRowOrNull())
    }

    @Test
    fun `row from index`() {
        assertSame(3.indexToRowOrNull(), Row.values[3])
        assertSame(5.indexToRow(), Row.values[5])
        assertEquals(null, 34.indexToRowOrNull())
        assertFailsWith<IndexOutOfBoundsException> { ROW_DIM.indexToRow() }
        assertEquals(null, (-3).indexToRowOrNull())
        assertFailsWith<IndexOutOfBoundsException> { (-1).indexToRow() }
    }
}
