package battleship.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import battleship.model.board.isComplete
import battleship.model.hasNotStarted
import battleship.model.hasStarted


/**
 * Function that displays all the menu UI
 *
 * @param model class that has all the information in the UI.
 * @param onExit function that knows what to do when exiting
 */
@Composable
fun FrameWindowScope.GameMenu(model: ModelView, onExit: () -> Unit) {
    MenuBar {
        Menu("Game") {
            Item(
                "Start",
                enabled = model.game.hasNotStarted && model.game.playerBoard.fleet.isComplete(),
                onClick = { model.start() })
            Item("Refresh", enabled = model.game.hasStarted && !model.autoRefreshEnabled, onClick = { model.refresh() })
            CheckboxItem(
                "Automatic refresh",
                checked = model.autoRefreshEnabled,
                enabled = model.game.hasStarted,
                onCheckedChange = { on -> model.setAutoRefresh(on) })
            Item("Exit", onClick = onExit)
        }
        Menu("Fleet") {
            Item(
                "Put All (remaining)",
                enabled = model.game.hasNotStarted && !model.game.playerBoard.fleet.isComplete(),
                onClick = { model.putAllRandom() })

            Item(
                "Remove All",
                enabled = model.game.hasNotStarted && model.game.playerBoard.fleet.isNotEmpty(),
                onClick = { model.removeAll() })
        }
    }
}

