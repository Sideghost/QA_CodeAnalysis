package battleship.ui

import battleship.model.*
import battleship.model.board.*
import battleship.model.ship.ShipType

private const val BOARD_CHAR_DIM = COLUMN_DIM * 2 + 1
private const val LETTERS_IDENT = 5
private const val HORIZONTAL_IDENT = 3
private const val BOARD_SEPARATION = 3

private const val horSep = '-'
private const val verSep = '|'
private const val cross = '+'

private const val CHAR_MISS = '!'
private const val CHAR_SHIP = '█' //'#'
private const val CHAR_HIT = '■'
private const val CHAR_SUNK = 'X'
private const val CHAR_WATER = ' '

private const val MESSAGE_WIN = "You WIN"
private const val MESSAGE_LOSE = "You LOSE"

// Horizontal separator condensed
val horizontalSeparators = CHAR_WATER.repeat(HORIZONTAL_IDENT) + cross + horSep.repeat(BOARD_CHAR_DIM) + cross

/**
 * Repeat the char [amount] times
 * @param amount amount of times to repeat the char
 */
fun Char.repeat(amount: Int) = "$this".repeat(amount)

/**
 * Returns the according char from the [Cell], blank space if it is none of the defined cells, meaning it is a "water" cell
 */
fun Cell?.toChar(): Char {
    return when (this) {
        is ShipSunk -> CHAR_SUNK
        is ShipHit -> CHAR_HIT
        is ShipCell -> CHAR_SHIP
        is MissCell -> CHAR_MISS
        else -> CHAR_WATER
    }
}

/**
 * Function to print the HELP command
 */
fun printHelp() {
    println("Available Commands:")
    println("\tHELP")
    println("\tPUT\t\t(<shipType> [<position> <align>] | all)")
    println("\tREMOVE\t\t<position> | all")
    println("\tGRID")
    println("\tSTART\t\t<name>")
    println("\tSHOT\t\t<position>")
    println("\tREFRESH")
    println("\tEXIT")
}

fun Board.printShipData(row: Int) {
    // check if row is in the row to print
    if (row !in 0 until ShipType.values.size) return

    val type = ShipType.values[row]
    val quantity = fleet.count { type === it.type }
    val squares = "$CHAR_SHIP".repeat(type.squares)
    print(" $quantity x $squares of ${type.fleetQuantity} (${type.name})")
}

fun Game.printStatus() {
    //Player turn or win status
    when (this) {
        is GameSetup -> {}
        is GameFight -> {
            if (playerBoard.lost()) {
                println(MESSAGE_LOSE)
            } else if (enemyBoard.fleet.isNotEmpty() && enemyBoard.lost()) {
                println(MESSAGE_WIN)
            }
            if (isYourTurn)
                println("Is your turn")
            else
                println("Wait for other (use refresh command)")

        }
    }
}

fun printHorizontalSeparators(twoSeparators: Boolean) {
    print(horizontalSeparators) // Print top separator
    if (twoSeparators)
        print(horizontalSeparators)
    println()
}

fun Game.printTop() {
    printColumnsIDX() // Prints column indexes
    if (this.hasStarted) {
        print(" ")
        printColumnsIDX()
    }
    println()
    printHorizontalSeparators(this.hasStarted)
}

/**
 * Function to print the [Game]
 */
fun Game.print() {

    printTop()

    repeat(ROW_DIM) {
        val playerBoard = playerBoard

        val rowNumber = Row.values[it].number.toString().padStart(2).padEnd(3)
        print(rowNumber)

        playerBoard.printRow(it, false)

        if (this is GameFight) {
            print(" ".repeat(BOARD_SEPARATION))
            enemyBoard.printRow(it, true)
        } else {
            playerBoard.printShipData(it)
        }
        println()
    }

    printHorizontalSeparators(this.hasStarted)

    printStatus()
}

fun printShotResult(result: ShotConsequence, hitType: ShipType?) {
    when (result) {
        ShotConsequence.HIT -> {
            println("Ship Hit")
        }

        ShotConsequence.MISS -> {
            println("Miss Shot")
        }

        ShotConsequence.SUNK -> {
            if (hitType != null) {
                println("${hitType.name} was Sunk")
            }
        }

        else -> {}
    }

}

/**
 * Prints the Column indexes of the board
 */
fun printColumnsIDX() {
    print(' '.repeat(LETTERS_IDENT))
    Column.values.forEach {
        print(it.letter.toString().padEnd(2))
    }
}

/**
 * Prints a single row of the board to the standard output
 * @param row index of the row to print
 * @param hide boolean that allows to hide enemy board
 */
fun Board?.printRow(row: Int, hide: Boolean) {
    if (this == null) {
        print(verSep)
        print(" ".repeat(COLUMN_DIM * 2 + 1))
        print(verSep)
    } else {
        print(verSep)
        repeat(COLUMN_DIM) { x ->
            val cell = grid[Position[x, row]]
            val char = cell.toChar()
            if (hide && char == CHAR_SHIP)
                print(" $CHAR_WATER")
            else
                print(" $char")

        }
        print(" ") // print final space
        print(verSep)
    }
}
