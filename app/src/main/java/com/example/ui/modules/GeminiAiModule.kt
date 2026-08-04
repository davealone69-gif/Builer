package com.example.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.ChatMessage

@Composable
fun GeminiAiModule(
    chatMessages: List<ChatMessage>,
    isLoading: Boolean,
    apiKeySetting: String,
    onApiKeyChanged: (String) -> Unit,
    onSendPrompt: (String) -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    var showApiKeySettings by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("gemini_ai_module"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Module Header with Key Settings toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gemini AI Assistant",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { showApiKeySettings = !showApiKeySettings }) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "API Key Settings",
                    tint = if (apiKeySetting.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Custom Key Drawer/Field
        if (showApiKeySettings) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Custom Gemini API Key Override (Optional)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = apiKeySetting,
                        onValueChange = onApiKeyChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gemini_key_input"),
                        label = { Text("Enter API Key") },
                        placeholder = { Text("AI Studio automatically injects key if present") },
                        singleLine = true
                    )
                }
            }
        }

        // Prompt Preset Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(
                onClick = { promptInput = "Refactor this code to clean Jetpack Compose M3 syntax." },
                modifier = Modifier.weight(1f)
            ) {
                Text("Refactor", style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = { promptInput = "Explain Room Database entity relationships and DAOs." },
                modifier = Modifier.weight(1f)
            ) {
                Text("Explain Room", style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = { promptInput = "Write a Kotlin Coroutine Flow unit test." },
                modifier = Modifier.weight(1f)
            ) {
                Text("Unit Test", style = MaterialTheme.typography.labelSmall)
            }
        }

        // Chat History Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini AI is processing response...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Input Field & Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("gemini_prompt_input"),
                placeholder = { Text("Ask Gemini AI coding question...") },
                singleLine = false,
                maxLines = 3
            )

            Button(
                onClick = {
                    if (promptInput.isNotBlank() && !isLoading) {
                        onSendPrompt(promptInput)
                        promptInput = ""
                    }
                },
                modifier = Modifier.testTag("send_gemini_prompt_btn"),
                enabled = promptInput.isNotBlank() && !isLoading
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Prompt")
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "User"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.sender,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = if (isUser) FontFamily.Default else FontFamily.Monospace
            )
        }
    }
}
