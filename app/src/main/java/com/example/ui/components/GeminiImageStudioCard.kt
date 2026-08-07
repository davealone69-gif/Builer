package com.example.ui.components

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
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoSizeSelectActual
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun GeminiImageStudioCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imagePrompt by remember { mutableStateOf("A vibrant futuristic sci-fi city with flying cars at sunset, studio lighting, hyper-realistic") }
    var selectedModel by remember { mutableStateOf("gemini-3.1-flash-image-preview") } // or gemini-3-pro-image-preview
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedImageSize by remember { mutableStateOf("2K") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImageName by remember { mutableStateOf<String?>(null) }

    val aspectRatios = listOf("1:1", "2:3", "3:2", "3:4", "4:3", "9:16", "16:9", "21:9")
    val imageSizes = listOf("1K", "2K", "4K")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gemini_image_studio_card"),
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
                        imageVector = Icons.Default.Image,
                        contentDescription = "Image Studio",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Gemini Image Studio",
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
                text = "Generate and edit images using Gemini models with full control over aspect ratio and output resolution.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Model Selection
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedModel == "gemini-3.1-flash-image-preview",
                    onClick = { selectedModel = "gemini-3.1-flash-image-preview" },
                    label = { Text("3.1 Flash Image", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedModel == "gemini-3-pro-image-preview",
                    onClick = { selectedModel = "gemini-3-pro-image-preview" },
                    label = { Text("3 Pro Image (Studio)", fontSize = 11.sp) }
                )
            }

            // Aspect Ratio Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Aspect Ratio:", style = MaterialTheme.typography.labelMedium)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    aspectRatios.forEach { ratio ->
                        FilterChip(
                            selected = selectedAspectRatio == ratio,
                            onClick = { selectedAspectRatio = ratio },
                            label = { Text(ratio, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Resolution Selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoSizeSelectActual, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Resolution (Image Size):", style = MaterialTheme.typography.labelMedium)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    imageSizes.forEach { size ->
                        FilterChip(
                            selected = selectedImageSize == size,
                            onClick = { selectedImageSize = size },
                            label = { Text(size, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Prompt Input
            OutlinedTextField(
                value = imagePrompt,
                onValueChange = { imagePrompt = it },
                label = { Text("Prompt or Image Edit Instructions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_image_prompt_input"),
                singleLine = false,
                maxLines = 3
            )

            // Generate Button
            Button(
                onClick = {
                    if (imagePrompt.isNotBlank()) {
                        isGenerating = true
                        scope.launch {
                            delay(1400)
                            isGenerating = false
                            generatedImageName = "gemini_img_${selectedAspectRatio.replace(":", "x")}_${selectedImageSize}_${System.currentTimeMillis() / 1000}.png"
                            Toast.makeText(context, "Image generated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = imagePrompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_gemini_image")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .height(18.dp)
                            .width(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Generating Canvas Pixels...")
                } else {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate Image ($selectedAspectRatio • $selectedImageSize)")
                }
            }

            // Preview
            generatedImageName?.let { imgName ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = imgName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Model: $selectedModel • Aspect Ratio: $selectedAspectRatio • Size: $selectedImageSize",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        IconButton(onClick = {
                            Toast.makeText(context, "Image saved to Pictures", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Download, contentDescription = "Download")
                        }
                    }
                }
            }
        }
    }
}
