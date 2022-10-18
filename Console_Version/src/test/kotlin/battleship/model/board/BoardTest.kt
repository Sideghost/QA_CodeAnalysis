package battleship.model.board

import battleship.model.PutConsequence
import battleship.model.ShotConsequence
import battleship.model.ship.ShipType
import kotlin.test.*

class BoardTest {

    @Test
    fun `empty board`() {
        val sut = Board()
        assertTrue {
            sut.grid.isEmpty() &&
                    sut.fleet.isEmpty()
        }
        assertEquals(0, sut.cellsQuantity())
        assertFalse { sut.fleet.isComplete() }
        assertTrue { sut.getPossiblePositions(1, Direction.VERTICAL).isNotEmpty() }
        assertTrue { sut.getPossiblePositions(1, Direction.HORIZONTAL).isNotEmpty() }
    }

    @Test
    fun `Place a ship`() {
        val sut = Board()
        Direction.values().forEach { dir ->
            ShipType.values.forEach { type ->
                val newBoard = sut.putShip(type, Position[0, 0], dir)
                assertSame(PutConsequence.NONE, newBoard.second)
                assertEquals(1, newBoard.first.fleet.size)
                assertEquals(type.squares, newBoard.first.grid.size)
            }
        }
    }

    @Test
    fun `Test place ship overlap`() {
        val type = ShipType.values.first { it.fleetQuantity > 1 }
        val sut = Board().putShip(type, Position[0, 0], Direction.HORIZONTAL).first
        val res = sut.putShip(type, Position[0, 0], Direction.HORIZONTAL)
        assertSame(PutConsequence.INVALID_POSITION, res.second)
        assertSame(sut, res.first)
    }

    @Test
    fun `Test place invalid position`() {
        val type = ShipType.values.first { it.squares > 1 }
        val newBoard = Board()
        val sut = newBoard.putShip(type, Position.values.last(), Direction.HORIZONTAL)
        assertSame(PutConsequence.INVALID_POSITION, sut.second)
        assertSame(newBoard, sut.first)
    }

    @Test
    fun `Test place invalid ship type`() {
        val type = ShipType.values.first { it.fleetQuantity == 1 }
        val base = Board().putShip(type, Position.values[0], Direction.HORIZONTAL).first
        val sut = base.putShip(type, Position[0, 6], Direction.HORIZONTAL)
        assertSame(sut.second, PutConsequence.INVALID_SHIP)
        assertSame(base, sut.first)
    }

    @Test
    fun `Test Remove`() {
        val type = ShipType.values.first()
        val base = Board()
        val sut = base.putShip(type, Position[0, 0], Direction.HORIZONTAL).first
        assertEquals(base, sut.removeShip(Position[0, 0]))
    }

    @Test
    fun `Test Remove Fail`() {
        val type = ShipType.values.first()
        val base = Board()
        val sut = base.putShip(type, Position[0, 0], Direction.HORIZONTAL).first
        assertEquals(sut, sut.removeShip(Position[5, 5]))
    }

    @Test
    fun `test shot on water`() {
        val base = Board()
        val sut = base.makeShot(Position.values[0])
        assertNotSame(base, sut.first)
        assertSame(ShotConsequence.MISS, sut.second)
        assertNull(sut.third)
    }

    @Test
    fun `test shot on ship`() {
        val base = Board().putShip(ShipType.values.first(), Position[0, 0], Direction.HORIZONTAL).first
        val sut = base.makeShot(Position.values[0])
        assertNotSame(base, sut.first)
        assertSame(ShotConsequence.HIT, sut.second)
        assertSame(ShipType.values.first(), sut.third)
    }
}
