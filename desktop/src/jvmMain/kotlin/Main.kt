import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


fun main(): Unit = application {
    Window(
        title = "MetaProbe KMP",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            size = DpSize(400.dp,1000.dp)
        )
    ) {
        App()
    }
}
