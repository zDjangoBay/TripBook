package com.android.tripbook

// 5. SPEECH TO TEXT COMPOSABLE - Create new file: SpeechToTextButton.kt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.Manifest

@Composable
fun SpeechToTextButton(
    onTextReceived: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    var speechManager by remember { mutableStateOf<SpeechRecognizerManager?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            speechManager = SpeechRecognizerManager(context)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    speechManager?.let { manager ->
        val isListening by manager.isListening
        val speechResult by manager.speechResult
        val error by manager.error

        LaunchedEffect(speechResult) {
            if (speechResult.isNotEmpty()) {
                onTextReceived(speechResult)
                manager.clearResult()
            }
        }

        IconButton(
            onClick = {
                if (isListening) {
                    manager.stopListening()
                } else {
                    manager.startListening()
                }
            },
            modifier = modifier
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop recording" else "Start recording",
                tint = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
            )
        }
    }

    DisposableEffect(speechManager) {
        onDispose {
            speechManager?.destroy()
        }
    }
}