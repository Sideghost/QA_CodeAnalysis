package battleship.model.ship

import battleship.model.board.Column.Companion.values
import battleship.model.board.Row.Companion.values
import battleship.model.ship.ShipType.Companion.values

/**
 * All ship types allowed in the game.
 * @property name [ShipType] name.
 * @property squares Number of squares occupied.
 * @property fleetQuantity Number of ships of this type available.
 * @property values companion object that has all the possible values for [ShipType]
 *
 */
class ShipType private constructor(val name: String, val squares: Int, val fleetQuantity: Int) {
    companion object {
        val values = listOf(
            ShipType("Carrier", 5, 1),
            ShipType("Battleship", 4, 2),
            ShipType("Cruiser", 3, 3),
            ShipType("Submarine", 2, 4)
        )
    }
}


/**
 * Returns a [ShipType] according to the string, if string is an integer, return a ship by number of squares
 *      else if it is a string, return the only [ShipType] that starts with the string as prefix
 *      else return null
 */
fun String.toShipTypeOrNull(): ShipType? {
    val num = this.toIntOrNull()
    return if (num == null) {
        val head = ShipType.values.firstOrNull { it.name.startsWith(this, true) }
        val tail = ShipType.values.lastOrNull { it.name.startsWith(this, true) }
        if (tail === head) tail else null
    } else {
        val head = ShipType.values.firstOrNull { it.squares == num || it.name.startsWith(this, true) }
        val tail = ShipType.values.lastOrNull { it.squares == num || it.name.startsWith(this, true) }
        if (tail === head) tail else null
    }
}

/**
 * Returns a [ShipType] according to the string, if string is an integer, return a ship by number of squares
 *          else if it is a string, return the only [ShipType] that starts with the string as prefix
 * @throws NoSuchElementException
 */
fun String.toShipType(): ShipType = toShipTypeOrNull() ?: throw NoSuchElementException()
