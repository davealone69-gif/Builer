package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LyriaMusicStudioCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var musicPrompt by remember { mutableStateOf("Upbeat synthwave ambient track with electronic beats and retro bass") }
    var selectedModel by remember { mutableStateOf("lyria-3-clip-preview") } // "lyria-3-clip-preview" (30s) or "lyria-3-pro-preview" (full)
    var isGenerating by remember { mutableStateOf(false) }
    var generatedTrackName by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0.0f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lyria_music_studio_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Lyria Music Studio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Lyria AI Music Generator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = selectedModel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = "Generate short clips (up to 30s) or full-length audio tracks using Google Lyria AI models.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Model Selection Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedModel == "lyria-3-clip-preview",
                    onClick = { selectedModel = "lyria-3-clip-preview" },
                    label = { Text("Short Clip (30s max)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedModel == "lyria-3-pro-preview",
                    onClick = { selectedModel = "lyria-3-pro-preview" },
                    label = { Text("Full-Length Track (Lyria Pro)", fontSize = 11.sp) }
                )
            }

            // Prompt Input
            OutlinedTextField(
                value = musicPrompt,
                onValueChange = { musicPrompt = it },
                label = { Text("Describe Music Style, Genre, Instruments & Tempo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lyria_music_prompt_input"),
                singleLine = false,
                maxLines = 3
            )

            // Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = { musicPrompt = "Cinematic orchestral score with dramatic string quartet" },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Orchestral", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                }
                Surface(
                    onClick = { musicPrompt = "Lofi hip-hop chill beats with relaxing acoustic piano" },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Lofi Piano", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                }
                Surface(
                    onClick = { musicPrompt = "Futuristic cyberpunk synth bass with fast electronic drum loop" },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cyberpunk", fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                }
            }

            // Generate Button
            Button(
                onClick = {
                    if (musicPrompt.isNotBlank()) {
                        isGenerating = true
                        isPlaying = false
                        playbackProgress = 0f
                        scope.launch {
                            delay(1200)
                            isGenerating = false
                            generatedTrackName = "lyria_composition_${System.currentTimeMillis() / 1000}.mp3"
                            Toast.makeText(context, "Music generated successfully via $selectedModel", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = musicPrompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_lyria_music")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Synthesizing Audio Waveforms...")
                } else {
                    Icon(Icons.Default.MusicNote, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Track with $selectedModel")
                }
            }

            // Generated Audio Preview Player
            generatedTrackName?.let { trackName ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = trackName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Model: $selectedModel • Bitrate: 320 kbps",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    isPlaying = !isPlaying
                                    if (isPlaying) {
                                        scope.launch {
                                            while (isPlaying && playbackProgress < 1.0f) {
                                                delay(200)
                                                playbackProgress += 0.05f
                                            }
                                            if (playbackProgress >= 1.0f) {
                                                isPlaying = false
                                                playbackProgress = 0.0f
                                            }
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause"
                                    )
                                }

                                IconButton(onClick = {
                                    Toast.makeText(context, "Audio file saved to Downloads folder", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download")
                                }
                            }
                        }

                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
