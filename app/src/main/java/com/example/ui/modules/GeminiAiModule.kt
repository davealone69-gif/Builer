package com.example.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.example.ui.components.ApiKeyGeneratorCard
import com.example.viewmodel.ApkArtifactSpec
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.GeneratedAppSpec

@Composable
fun GeminiAiModule(
    chatMessages: List<ChatMessage>,
    isLoading: Boolean,
    apiKeySetting: String,
    builderPrompt: String,
    isBuildingApp: Boolean,
    buildLogs: List<String>,
    generatedAppSpec: GeneratedAppSpec?,
    apkArtifact: ApkArtifactSpec?,
    onApiKeyChanged: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    onBuilderPromptChanged: (String) -> Unit,
    onGenerateAndBuildApp: (String) -> Unit
) {
    var subTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("gemini_ai_module"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tab Row for App Builder vs Chat Assistant
        TabRow(
            selectedTabIndex = subTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Tab(
                selected = subTabIndex == 0,
                onClick = { subTabIndex = 0 },
                modifier = Modifier.testTag("tab_app_builder_prompt"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("App Builder & APK Engine", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = subTabIndex == 1,
                onClick = { subTabIndex = 1 },
                modifier = Modifier.testTag("tab_ai_chat"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("AI Assistant Chat")
                    }
                }
            )
        }

        if (subTabIndex == 0) {
            // App Builder Prompt & APK Engine View
            AppBuilderPromptView(
                builderPrompt = builderPrompt,
                isBuildingApp = isBuildingApp,
                buildLogs = buildLogs,
                generatedAppSpec = generatedAppSpec,
                apkArtifact = apkArtifact,
                onPromptChanged = onBuilderPromptChanged,
                onGenerateAndBuild = onGenerateAndBuildApp
            )
        } else {
            // Chat Assistant View
            ChatAssistantView(
                chatMessages = chatMessages,
                isLoading = isLoading,
                apiKeySetting = apiKeySetting,
                onApiKeyChanged = onApiKeyChanged,
                onSendPrompt = onSendPrompt
            )
        }
    }
}

@Composable
private fun AppBuilderPromptView(
    builderPrompt: String,
    isBuildingApp: Boolean,
    buildLogs: List<String>,
    generatedAppSpec: GeneratedAppSpec?,
    apkArtifact: ApkArtifactSpec?,
    onPromptChanged: (String) -> Unit,
    onGenerateAndBuild: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Command Prompt Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = "Builder Prompt",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Android App Builder Command Prompt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = builderPrompt,
                    onValueChange = onPromptChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("app_builder_prompt_input"),
                    label = { Text("Describe complete Android app to generate & compile") },
                    placeholder = { Text("e.g. Build a Crypto Portfolio Tracker with Jetpack Compose & Room DB") }
                )

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onPromptChanged("Build an Expense Tracker App with Room DB and M3 Jetpack Compose") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Expense Tracker", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { onPromptChanged("Build a Crypto Portfolio Manager with Retrofit API and Charts") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Crypto Wallet", style = MaterialTheme.typography.labelSmall)
                    }
                    OutlinedButton(
                        onClick = { onPromptChanged("Build a Fitness Workout Tracker with Material 3 UI") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fitness App", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Button(
                    onClick = { onGenerateAndBuild(builderPrompt) },
                    enabled = builderPrompt.isNotBlank() && !isBuildingApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_build_apk_btn")
                ) {
                    if (isBuildingApp) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Building Android APK...")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Generate Full Code & Build APK File")
                    }
                }
            }
        }

        // Generated Android Application Breakdown Card
        generatedAppSpec?.let { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Generated App: ${app.appName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = app.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Main Source File (MainActivity.kt):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = app.mainActivityCode,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp)
                    )
                }
            }
        }

        // Real Output Artifact Details Card
        apkArtifact?.let { apk ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Compiled APK Artifact Ready",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = apk.fileSize,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = """
                            APK File: ${apk.fileName}
                            Output Path: ${apk.filePath}
                            Target Build Task: ${apk.variant}
                            Signed: ${if (apk.isSigned) "Yes (Verified Debug Signature)" else "No"}
                            GitHub Actions Artifact: ${apk.ciWorkflowUrl}
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("download_apk_btn")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Download APK")
                        }
                    }
                }
            }
        }

        // Live Gradle Build Log Terminal Console
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Build Console",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Gradle Compile Terminal Output",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = buildLogs.joinToString("\n"),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatAssistantView(
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gemini Code & Architecture Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { showApiKeySettings = !showApiKeySettings }) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "API Key Settings",
                    tint = if (apiKeySetting.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showApiKeySettings) {
            ApiKeyGeneratorCard(
                currentApiKey = apiKeySetting,
                onApiKeyGenerated = onApiKeyChanged
            )
        }

        // Chat History
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
