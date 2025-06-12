import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.DatabaseManager
import ui.PasswordRecoveryApp
import ui.theme.PasswordRecoveryTheme

fun main() = application {
    // Initialize database
    DatabaseManager.initDatabase()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Password Recovery System",
        state = WindowState(
            width = 1000.dp,
            height = 700.dp,
            position = WindowPosition(Alignment.Center)
        )
    ) {
        PasswordRecoveryTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PasswordRecoveryApp()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    PasswordRecoveryTheme {
        PasswordRecoveryApp()
    }
}
