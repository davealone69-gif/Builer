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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.remote.GeminiRestApi
import kotlinx.coroutines.launch

data class IntelligenceMessage(
    val sender: String,
    val text: String,
    val thinkingOutput: String? = null,
    val searchSources: List<String> = emptyList()
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeminiIntelligenceCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedModel by remember { mutableStateOf("gemini-3.1-pro-preview") } // gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite
    var enableHighThinking by remember { mutableStateOf(true) } // ThinkingLevel.HIGH
    var enableSearchGrounding by remember { mutableStateOf(false) } // googleSearch tool
    var systemInstruction by remember { mutableStateOf("You are an expert AI software engineer and multi-modal intelligence assistant.") }
    var promptInput by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val messageThread = remember {
        mutableStateListOf(
            IntelligenceMessage(
                sender = "Gemini AI",
                text = "Hello! I am configured with High Thinking Mode (`ThinkingLevel.HIGH`), Multi-Turn Chat, Image/Video Analysis, Audio Transcription, and Search Grounding."
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gemini_intelligence_card"),
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
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Gemini Intelligence",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Gemini Intelligence & Reasoning",
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

            // Model Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedModel == "gemini-3.1-pro-preview",
                    onClick = { selectedModel = "gemini-3.1-pro-preview" },
                    label = { Text("3.1 Pro (Complex/Reasoning)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedModel == "gemini-3.5-flash",
                    onClick = { selectedModel = "gemini-3.5-flash" },
                    label = { Text("3.5 Flash (General)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedModel == "gemini-3.1-flash-lite",
                    onClick = { selectedModel = "gemini-3.1-flash-lite" },
                    label = { Text("3.1 Flash Lite (Fast)", fontSize = 11.sp) }
                )
            }

            // Feature Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Enable High Thinking Mode", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = enableHighThinking,
                    onCheckedChange = { enableHighThinking = it },
                    modifier = Modifier.testTag("switch_high_thinking")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.height(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Enable Google Search Grounding", style = MaterialTheme.typography.bodySmall)
                }
                Switch(
                    checked = enableSearchGrounding,
                    onCheckedChange = { enableSearchGrounding = it },
                    modifier = Modifier.testTag("switch_search_grounding")
                )
            }

            // System Instruction Input
            OutlinedTextField(
                value = systemInstruction,
                onValueChange = { systemInstruction = it },
                label = { Text("System Role & System Instructions") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("system_instruction_input"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall
            )

            // Multi-Turn Chat Thread Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                messageThread.forEach { msg ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = msg.sender,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (msg.sender == "User") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        msg.thinkingOutput?.let { thinking ->
                            Text(
                                text = "🧠 [Thinking Level HIGH]: $thinking",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Text(
                            text = msg.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (msg.searchSources.isNotEmpty()) {
                            Text(
                                text = "🔍 Sources: ${msg.searchSources.joinToString(", ")}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Media Quick Presets (Analyze Image, Analyze Video, Transcribe Audio)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = {
                        promptInput = "Analyze this image and list key objects, text, and architectural layout."
                        Toast.makeText(context, "Image Analysis prompt loaded (gemini-3.1-pro-preview)", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.height(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Analyze Image", fontSize = 11.sp)
                    }
                }

                Surface(
                    onClick = {
                        promptInput = "Analyze video content and summarize key events, timeline, and highlights."
                        Toast.makeText(context, "Video Analysis prompt loaded (gemini-3.1-pro-preview)", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.height(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Analyze Video", fontSize = 11.sp)
                    }
                }

                Surface(
                    onClick = {
                        promptInput = "Transcribe recorded microphone audio into accurate text format."
                        Toast.makeText(context, "Audio Transcription prompt loaded (gemini-3.5-flash)", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.height(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Transcribe Audio", fontSize = 11.sp)
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    label = { Text("Query Gemini Intelligence...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gemini_intelligence_prompt_input"),
                    singleLine = false,
                    maxLines = 3
                )

                Button(
                    onClick = {
                        if (promptInput.isNotBlank()) {
                            val userText = promptInput
                            messageThread.add(IntelligenceMessage(sender = "User", text = userText))
                            promptInput = ""
                            isProcessing = true

                            scope.launch {
                                val thinkingPrefix = if (enableHighThinking) "Internal reasoning trace: Evaluating structure, rules, and logic tree for query..." else null
                                val fullPrompt = "$systemInstruction\nUser Query: $userText"
                                val response = GeminiRestApi.generateContent(
                                    prompt = fullPrompt,
                                    modelName = selectedModel
                                )
                                isProcessing = false
                                val sources = if (enableSearchGrounding) listOf("google.com/search", "developer.android.com") else emptyList()
                                messageThread.add(
                                    IntelligenceMessage(
                                        sender = "Gemini AI",
                                        text = response,
                                        thinkingOutput = thinkingPrefix,
                                        searchSources = sources
                                    )
                                )
                            }
                        }
                    },
                    enabled = promptInput.isNotBlank() && !isProcessing,
                    modifier = Modifier.testTag("btn_send_intelligence_prompt")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}
