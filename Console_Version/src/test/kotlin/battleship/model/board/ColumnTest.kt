package battleship.model.board

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * The column type identifies one of the game's grid columns.
 * Columns are identified by a letter from 'A' to 'J', if the grid has 10 columns.
 * The leftmost column is identified by the letter 'A'.
 */
class ColumnTest { 
    @Test
    fun `number of columns`() {
        assertEquals(10, COLUMN_DIM) // const
        assertEquals(COLUMN_DIM, Column.values.size) // const
    }

    @Test
    fun `letter of each column`() {
        assertEquals('A', Column.values.first().letter)
        assertEquals('J', Column.values.last().letter)
        assertEquals("ABCDEFGHIJ".toList(), Column.values.map { it.letter })
    }

    @Test
    fun `ordinal of column`() {
        assertEquals(0, Column.values.first().ordinal)
        assertEquals(COLUMN_DIM - 1, Column.values.last().ordinal)
        assertEquals((0 until COLUMN_DIM).toList(), Column.values.map { it.ordinal })
    }

    @Test
    fun `column from letter`() {
        assertSame('A'.toColumnOrNull(), Column.values[0])
        assertSame('j'.toColumnOrNull(), Column.values[COLUMN_DIM - 1])
        assertEquals(null, 'X'.toColumnOrNull())
    }

    @Test
    fun `column from index`() {
        assertSame(3.indexToColumnOrNull(), Column.values[3])
        assertSame(5.indexToColumn(), Column.values[5])
        assertEquals(null, 34.indexToColumnOrNull())
        assertFailsWith<IndexOutOfBoundsException> { COLUMN_DIM.indexToColumn() }
        assertEquals(null, (-3).indexToColumnOrNull())
        assertFailsWith<IndexOutOfBoundsException> { (-1).indexToColumn() }
    }
}
