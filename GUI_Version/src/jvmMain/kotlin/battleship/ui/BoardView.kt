package battleship.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import battleship.model.board.*

const val BOARD_LINE_SIZE = 1
const val BOARD_WIDTH = (BOARD_CELL_SIZE + BOARD_LINE_SIZE) * (COLUMN_DIM + 2)
const val BOARD_HEIGHT = (BOARD_CELL_SIZE + BOARD_LINE_SIZE) * (ROW_DIM + 1)

/**
 * Function that contains the UI related only the Cells in a board.
 *
 * @param board model of the Board to display
 * @param hidden boolean that tells if the board displaying should be hidden
 * @param canClick boolean that tells if you can click the cells on the board
 * @param onClickCell function that knows what to do when a cell is clicked in a certain [Position]
 */
@Composable
fun BoardView(board: Board, hidden: Boolean, canClick: Boolean, onClickCell: (Position) -> Unit) {
    Column {
        repeat(ROW_DIM) { line ->
            if (line != 0) Spacer(Modifier.height(BOARD_LINE_SIZE.dp)) //Add spacer in every row except the first
            Row {
                // Board
                repeat(COLUMN_DIM) { col ->
                    if (col != 0) Spacer(Modifier.width(BOARD_LINE_SIZE.dp))
                    val pos = Position[col, line]
                    val cell = board.grid[pos]
                    CellView(cell, hidden, canClick) { onClickCell.invoke(pos) }
                }
            }
        }
    }
}

/**
 * Function that displays an identifier (letter or number) inside a box
 *
 * @param str identifier to print
 */
@Composable
fun IdentifierView(str: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(BOARD_CELL_SIZE.dp)) {
        Text(textAlign = TextAlign.Center, text = str)
    }
}

/**
 * Function that displays an entire row with the letter identifiers
 */
@Composable
fun LetterAxisView() {
    Row {
        repeat(COLUMN_DIM) {
            val letter = it.indexToColumn().letter.toString()
            IdentifierView(letter)
            Spacer(Modifier.size(BOARD_LINE_SIZE.dp))
        }
    }
}

/**
 * Function that displays an entire column with the number identifiers
 */
@Composable
fun NumberAxisView() {
    Column {
        repeat(ROW_DIM) {
            val number = it.indexToRow().number.toString()
            IdentifierView(number)
            Spacer(Modifier.size(BOARD_LINE_SIZE.dp))
        }
    }
}

/**
 * Function that contains all the UI related to displaying one board, including spacers and identifiers
 *
 * @param board model of the Board to display
 * @param hidden boolean that tells if the board displaying should be hidden
 * @param canClick boolean that tells if you can click the cells on the board
 * @param onClickCell function that knows what to do when a cell is clicked in a certain [Position]
 */
@Composable
fun BoardWithGuidesView(board: Board, hidden: Boolean, canClick: Boolean, onClickCell: (Position) -> Unit) {
    Column(Modifier.background(Color.White).width(BOARD_WIDTH.dp).height(BOARD_HEIGHT.dp)) {
        //ROW with column identifiers
        Row {
            // Initial Spacing
            Spacer(modifier = Modifier.size(BOARD_CELL_SIZE.dp))

            // Upper letters
            LetterAxisView()
        }
        //ROW with board and row identifiers
        Row {
            // Left side numbers
            NumberAxisView()
            // Board 
            BoardView(board, hidden, canClick, onClickCell)
        }
    }
}