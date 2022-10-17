package battleship.model

import battleship.model.board.COLUMN_DIM
import battleship.model.board.ROW_DIM
import battleship.model.ship.ShipType
import battleship.model.ship.toShipType
import battleship.model.ship.toShipTypeOrNull
import kotlin.test.*

/**
 * The f1.ShipType type identifies types of ships allowed in game.
 */
class ShipTypeTest {
    @Test
    fun `Each ship type has a different name and number of squares`() {
        val numberOfTypes = ShipType.values.size
        assertEquals(numberOfTypes, ShipType.values.distinctBy { it.squares }.size)
        assertEquals(numberOfTypes, ShipType.values.distinctBy { it.name }.size)
    }

    @Test
    fun `get ShipType by number of squares`() {
        val sut = "2".toShipTypeOrNull() // One ship has 2 squares ?
        assertNotNull(sut) // Yes
        assertEquals(2, sut.squares) // Confirm number of squares
        assertEquals("Submarine", sut.name) // Its name is "Submarine"
        assertSame("2".toShipType(), sut)
    }

    @Test
    fun `get ShipType by name prefix`() {
        val sut = "Carr".toShipTypeOrNull() // One ship with "Carr" prefix ?
        assertNotNull(sut) // Yes
        assertEquals(5, sut.squares) // number of squares is 5
        assertEquals(1, sut.fleetQuantity) // One "Carrier" in fleet
        assertSame("Carrier".toShipType(), sut)
    }

    @Test
    fun `get ShipType fails`() {
        assertEquals(null, "0".toShipTypeOrNull()) // No ships with 0 squares
        assertEquals(null, "Ab".toShipTypeOrNull()) // No ships with "Ab" prefix
        // No ships with "X" prefix
        assertFailsWith<NoSuchElementException> { "X".toShipType() }
        // More than 1 ship with "C" prefix
        assertFailsWith<NoSuchElementException> { "C".toShipType() }
    }

    @Test
    fun `Make sure all ships are valid`() {
        for (ship in ShipType.values) {
            assertTrue { ship.squares >= 1 }
            assertTrue { ship.squares <= COLUMN_DIM && ship.squares <= ROW_DIM } // a ship must be smaller than the board to be playable
        }
    }
}
