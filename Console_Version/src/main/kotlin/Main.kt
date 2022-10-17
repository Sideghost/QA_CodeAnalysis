import battleship.model.Game
import battleship.model.createEmptyGame
import battleship.storage.MongoStorage
import battleship.ui.getCommandsOO
import battleship.ui.readCommand
import com.mongodb.MongoException
import mongoDB.MongoDriver
import kotlin.system.exitProcess

private const val DATABASE_NAME = "BattleShip"

/**
 * Main function that will be run in order to play the BattleShip Game
 */
fun main() {
    try {
        MongoDriver(DATABASE_NAME).use { drv ->
            var game: Game = createEmptyGame()
            val cmds = getCommandsOO(MongoStorage(drv))
            while (true) {
                val (name, args) = readCommand()
                val cmd = cmds[name]
                if (cmd == null) {
                    println("Invalid Command! \nUse the Command HELP for the available command list")
                } else {
                    try {
                        game = cmd.action(game, args) ?: exitProcess(0)
                        cmd.show(game)
                    } catch (ex: IllegalStateException) {
                        println(ex.message)
                    } catch (ex: IllegalArgumentException) {
                        println(ex.message)
                        println("Use: $name ${cmd.argsSyntax}")
                    }
                }
            }
        }
    } catch (ex: MongoException) {
        println("An exception has occurred with MongoDB!")
        println("Exception message: ${ex.message}")
    } catch (ex: Exception) {
        println("An unknown exception has ocurred!")
        println("Exception message: ${ex.message}")
    } finally {
        println("Bye!")
    }
}
