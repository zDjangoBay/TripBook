package com.android.tripbook.ui.components.common


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AudioRecorder(onAudioRecorded: (String?) -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingPath by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (isRecording) {
                    isRecording = false
                    recordingPath = "/path/to/recorded_audio_${System.currentTimeMillis()}.mp3"
                    onAudioRecorded(recordingPath)
                } else {
                    isRecording = true
                    recordingPath = null
                }
            },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Arrêter l'enregistrement" else "Démarrer l'enregistrement",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = if (isRecording) "Enregistrement en cours..." else "Appuyez pour enregistrer un audio-guide")

        recordingPath?.let { path ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Audio enregistré: $path")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAudioRecorder() {
    AudioRecorder(onAudioRecorded = { path ->
        println("Audio recorded at: $path")
    })
}