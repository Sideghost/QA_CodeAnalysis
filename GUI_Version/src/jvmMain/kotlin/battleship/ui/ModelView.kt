package battleship.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import battleship.model.*
import battleship.model.board.Direction
import battleship.model.board.Position
import battleship.model.ship.ShipType
import battleship.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val MESSAGE_TIMER = 3000L
const val AUTO_REFRESH_TIMER = 1000L

const val STATUS_WARN_INVALID_PUT_SHIP = "Ship type all used up"
const val STATUS_WARN_INVALID_PUT_POSITION = "Invalid Position"
const val STATUS_WARN_INVALID_SHOT = "Invalid Shot"
const val STATUS_WARN_INVALID_SHOT_WAIT_FOR_OTHER = "Wait for the enemy to start"
const val STATUS_WARN_INVALID_SHOT_TURN = "It's not your turn"

/**
 * Abstraction for game model, and offers functionalities for composable application, enjoy Battleship!
 */
class ModelView(private val storage: Storage, private val scope: CoroutineScope) {

    var game by mutableStateOf<Game>(createEmptyGame())
        private set

    var openDialogName by mutableStateOf(false)
        private set

    // for pop-up window
    var warning by mutableStateOf<String?>(null)
        private set

    // for status bar message
    var message by mutableStateOf<String?>(null)
        private set

    // coroutine to show message on status bar
    private var messageJob by mutableStateOf<Job?>(null)

    // auto refresh coroutine
    var jobAutoRefresh by mutableStateOf<Job?>(null)
        private set

    var autoRefreshEnabled by mutableStateOf(true)
        private set

    // selected type to place ships with
    var selectedType by mutableStateOf<ShipType?>(ShipType.values.first())
        private set

    // selected direction to place ships with
    var selectedDirection by mutableStateOf(Direction.HORIZONTAL)
        private set

    /**
     * Displays a message for [MESSAGE_TIMER] milliseconds, afterwards it turns off, if another message is
     * displaying during this time, it will override it.
     *
     * @param msg to be shown in the status bar.
     */
    private fun sendMessage(msg: String) {
        messageJob?.cancel()
        message = msg
        messageJob = scope.launch { delay(MESSAGE_TIMER); message = null }
    }

    /**
     * Warning message is turned off.
     */
    fun warningAck() {
        warning = null
    }

    /**
     * Function to assess the current phase of the [game].
     *
     * @throws IllegalStateException if [game] is not of type [T].
     */
    inline fun <reified T> getGame(): T =
        with(game) {
            check(this is T)
            return this
        }

    /**
     * Function that refreshes the game form [Storage].
     */
    fun refresh() {
        scope.launch { game = storage.load(getGame()) }
    }

    /**
     * Function that starts a game wih persistence in a [Storage].
     *
     * @param name name of the game to start
     * @throws IllegalStateException if [game] is not of GameSetup phase.
     */
    fun start(name: String? = null) {
        if (name == null) {
            openDialogName = true
        } else {

            if (!name.all { it.isWhitespace() || it.isLetterOrDigit() }) {
                warning = "Game name should only have alphanumeric characters and whitespaces"
                return
            }
            val currGame = getGame<GameSetup>()
            scope.launch {
                game = currGame.startGame(name, storage)
                openDialogName = false
                waitForOther()
            }
        }
    }

    /**
     * Function that handles the auto refresh of the game.
     *
     * @param auto boolean if auto refresh is on or not.
     */
    fun setAutoRefresh(auto: Boolean) {
        autoRefreshEnabled = auto
        if (autoRefreshEnabled) {
            waitForOther()
        } else {
            cancelAutoRefresh()
        }
    }

    /**
     * Function that has the logic condition if you should auto refresh or not.
     * @param g Game to auto refresh.
     */
    private fun shouldAutoRefresh(g: GameFight) =
        (g.isNotYourTurn || (g.isYourTurn && g.enemyBoard.fleet.isEmpty())) && g.winner == null

    /**
     * If necessary, launch a coroutine to do periodic readings until it's the turn to play.
     * To call at the start of the game and after each move.
     *
     * @throws IllegalStateException if [game] is not of GameFight phase.
     */
    private fun waitForOther() {
        var g = getGame<GameFight>()
        if (autoRefreshEnabled && !shouldAutoRefresh(g)) return
        jobAutoRefresh = scope.launch {
            do {
                delay(AUTO_REFRESH_TIMER)
                g = storage.load(g)
                game = g
            } while (shouldAutoRefresh(g))
            jobAutoRefresh = null
        }
    }

    /**
     * Function that cancels the auto refresh.
     */
    private fun cancelAutoRefresh() {
        jobAutoRefresh?.cancel()
        jobAutoRefresh = null
        autoRefreshEnabled = false
    }

    /**
     * Function that puts a ship in [pos] if possible.
     *
     * @param pos position to put the ship.
     * @throws IllegalStateException if [game] is not of GameSetup phase.
     */
    fun putShip(pos: Position) {
        val currGame = getGame<GameSetup>()

        val type = selectedType ?: return
        val dir = selectedDirection

        val result = currGame.putShip(type, pos, dir)

        if (result.second === PutConsequence.NONE) {
            game = result.first
        } else {
            when (result.second) {
                PutConsequence.INVALID_SHIP -> sendMessage(STATUS_WARN_INVALID_PUT_SHIP)
                PutConsequence.INVALID_POSITION -> sendMessage(STATUS_WARN_INVALID_PUT_POSITION)
                else -> {}
            }
        }

        selectAvailableType()
    }

    /**
     * Function that removes a ship if possible.
     *
     * @param pos target [Position] to attempt to remove ship.
     *
     * @throws IllegalStateException if [game] is not a [GameSetup].
     */
    fun removeShip(pos: Position) {
        with(getGame<GameSetup>())
        {
            game = removeShip(pos)
            selectAvailableType()
        }
    }

    /**
     * Function that puts all the remaining ships in random positions.
     *
     * @throws IllegalStateException if [game] is not a [GameSetup].
     */
    fun putAllRandom() {
        with(getGame<GameSetup>())
        {
            val res = this.putAllShips()
            if (res.second === PutConsequence.NONE) {
                game = res.first
                selectAvailableType()
            }
        }
    }

    /**
     * Function to remove all ships in the [GameSetup] phase, also updates selected type.
     *
     * @throws IllegalStateException if [game] is not of GameSetup phase.
     */
    fun removeAll() {
        with(getGame<GameSetup>())
        {
            game = removeAll()
            selectedType = ShipType.values.first()
        }
    }

    /**
     * Function to make a shot in the enemy board.
     *
     * @param pos Position to hit the enemy board.
     * @throws IllegalStateException if [game] is not [GameFight].
     */
    fun makeShot(pos: Position) {
        with(getGame<GameFight>()) {
            if (winner != null) return

            if (this.enemyBoard.fleet.isEmpty()) {
                sendMessage(STATUS_WARN_INVALID_SHOT_WAIT_FOR_OTHER)
                return
            }
            val shotResult = makeShot(pos, storage, scope)
            if (shotResult.second !== ShotConsequence.INVALID && shotResult.second !== ShotConsequence.NOT_YOUR_TURN) {
                game = shotResult.first
                waitForOther()
            } else {
                when (shotResult.second) {
                    ShotConsequence.NOT_YOUR_TURN -> sendMessage(STATUS_WARN_INVALID_SHOT_TURN)
                    ShotConsequence.INVALID -> sendMessage(STATUS_WARN_INVALID_SHOT)
                    else -> {}
                }
            }
        }
    }

    /**
     * Updates the model view with a new selected type, in case the type can not be selected (e.g. no more available
     * ships of that type) [selectedType] will be null.
     *
     * @param type the [ShipType] to be set.
     */
    fun setShipType(type: ShipType) {
        selectedType = type.takeIf { shipType ->
            game.playerBoard.fleet.count { shipType === it.type } < type.fleetQuantity
        }
    }

    /**
     * Updates the model view with a new available type, in case the type can not be selected (e.g. no more available
     * ships of that type) [selectedType] will be null.
     */
    private fun selectAvailableType() {
        selectedType = ShipType.values.firstOrNull { shipType ->
            game.playerBoard.fleet.count { shipType === it.type } < shipType.fleetQuantity
        }
    }

    /**
     * Updates the model view with a new direction.
     *
     * @param direction to update.
     */
    fun setDirection(direction: Direction) {
        selectedDirection = direction
    }

    /**
     * Updates the model mutable state [openDialogName] with false.
     */
    fun closeDialog() {
        openDialogName = false
    }
}
