package battleship.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState

val DIALOG_COLOR = Color.Red

/**
 * Function that contains all the UI needed to display the input dialog with the warning
 *
 * @param message string to display in the warning
 * @param onOk function that knows what to do when the dialog is canceled
 */
@Composable
fun DialogWarning(message: String, onOk: () -> Unit) = Dialog(
    onCloseRequest = onOk,
    title = "Warning",
    state = DialogState(height = Dp.Unspecified, width = DIALOG_WIDTH.dp)
) {
    ContentMessage(message, onOk)
}

/**
 * Function that displays the message to the warning
 *
 * @param message message to display
 * @param onOk function to execute when pressing the Ok button
 */
@Composable
private fun ContentMessage(message: String, onOk: () -> Unit) = Column(
    Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(text = message, fontWeight = FontWeight.Bold, color = DIALOG_COLOR)
    Button(onClick = onOk) { Text("Ok") }
}