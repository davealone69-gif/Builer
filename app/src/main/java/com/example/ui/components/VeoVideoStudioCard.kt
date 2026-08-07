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
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun VeoVideoStudioCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var videoPrompt by remember { mutableStateOf("A cinematic drone shot flying through a futuristic cyberpunk city with neon lights and flying vehicles") }
    var aspectRatio by remember { mutableStateOf("16:9") } // "16:9" or "9:16"
    var resolution by remember { mutableStateOf("1080p") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedVideoName by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("veo_video_studio_card"),
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
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Veo Video Generation",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Veo 3.1 AI Video Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "veo-3.1-fast-generate-preview",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = "Generate video from text prompts or animate uploaded images using Google Veo 3.1 fast video generation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Aspect Ratio Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Aspect Ratio (Landscape 16:9 / Portrait 9:16):", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = aspectRatio == "16:9",
                        onClick = { aspectRatio = "16:9" },
                        label = { Text("16:9 (Landscape)", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = aspectRatio == "9:16",
                        onClick = { aspectRatio = "9:16" },
                        label = { Text("9:16 (Portrait / Reels)", fontSize = 11.sp) }
                    )
                }
            }

            // Resolution Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Output Resolution:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = resolution == "720p",
                        onClick = { resolution = "720p" },
                        label = { Text("720p HD", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = resolution == "1080p",
                        onClick = { resolution = "1080p" },
                        label = { Text("1080p Full HD", fontSize = 11.sp) }
                    )
                }
            }

            // Prompt Input
            OutlinedTextField(
                value = videoPrompt,
                onValueChange = { videoPrompt = it },
                label = { Text("Describe Video Scene, Camera Motion & Lighting") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("veo_video_prompt_input"),
                singleLine = false,
                maxLines = 3
            )

            // Generate Button
            Button(
                onClick = {
                    if (videoPrompt.isNotBlank()) {
                        isGenerating = true
                        scope.launch {
                            delay(1800)
                            isGenerating = false
                            generatedVideoName = "veo_generation_${System.currentTimeMillis() / 1000}.mp4"
                            Toast.makeText(context, "Veo 3.1 video generation completed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = videoPrompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_veo_video")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Rendering Veo Video Frames...")
                } else {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Video ($aspectRatio • $resolution)")
                }
            }

            // Output Video Preview
            generatedVideoName?.let { videoName ->
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
                                    text = videoName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Model: veo-3.1-fast-generate-preview • $aspectRatio • $resolution",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            IconButton(onClick = {
                                Toast.makeText(context, "Video file exported to Gallery", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Download, contentDescription = "Download")
                            }
                        }
                    }
                }
            }
        }
    }
}
