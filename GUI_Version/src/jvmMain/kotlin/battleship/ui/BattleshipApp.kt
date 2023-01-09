package battleship.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import battleship.model.GameFight
import battleship.model.board.Direction
import battleship.model.board.Position
import battleship.model.board.ShipCell
import battleship.model.hasNotStarted
import battleship.model.ship.ShipType
import battleship.model.winner
import battleship.storage.Storage

private const val SPACER = 5

/**
 * Function that contains all the UI of the Battleship Game
 *
 * @param storage current storage in use for the application (file or MongoDB)
 * @param onExit onExit function that knows what to do when the app is closed
 */
@Composable
fun FrameWindowScope.BattleshipApp(storage: Storage, onExit: () -> Unit) {
    val scope = rememberCoroutineScope()
    val model = remember { ModelView(storage, scope) }
    MaterialTheme {
        GameMenu(model, onExit = onExit)
        if (model.openDialogName) {
            DialogName(onCancel = { model.closeDialog() }) {
                val trimmed = it.trim()
                model.start(trimmed)
            }
            model.warning?.let {
                DialogWarning(it) { model.warningAck() }
            }
        }

        Column {
            Row(modifier = Modifier.width(((BOARD_WIDTH * 2)).dp).height(BOARD_HEIGHT.dp)) {
                ///////////////
                // Left Side //
                ///////////////
                val onClickCell: ((Position) -> Unit) =
                    { pos ->
                        if (model.game.hasNotStarted) {
                            val cell = model.game.playerBoard.grid[pos]
                            if (cell is ShipCell)
                                model.removeShip(pos)
                            else
                                model.putShip(pos)
                        }
                    }
                BoardWithGuidesView(model.game.playerBoard, false, model.game.hasNotStarted, onClickCell)

                ////////////////
                // Right Side //
                ////////////////
                if (model.game.hasNotStarted) {
                    val onClickShip: (ShipType) -> Unit = { type ->
                        model.setShipType(type)
                    }
                    val onClickDir: (Direction) -> Unit = { dir ->
                        model.setDirection(dir)
                    }
                    SideView(
                        model.game.playerBoard.fleet,
                        onClickShip,
                        model.selectedType,
                        onClickDir,
                        model.selectedDirection
                    )
                } else {
                    val onClickEnemyCell: (Position) -> Unit = { pos ->
                        model.makeShot(pos)
                    }
                    val enemyBoard = model.getGame<GameFight>().enemyBoard
                    BoardWithGuidesView(enemyBoard, true, model.getGame<GameFight>().winner == null, onClickEnemyCell)
                }
            }
            //Bottom Part with a row for the status view
            Spacer(Modifier.size(SPACER.dp))
            StatusView(model, model.message)
        }
    }
}
