package com.example.ui.modules

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Data model for re-imagined glitch history item
data class GlitchHistoryItem(
    val id: String,
    val timestamp: Long,
    val sourceImageName: String,
    val prompt: String,
    val mode: String, // "Mandela", "Matrix", or "Dual"
    val realityStrength: Float,
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MandelaCoreModule(
    quantumNodes: List<Pair<String, String>>,
    onCycleState: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Screen Tabs: 0 = Splash/Onboarding, 1 = Home/Create, 2 = Result View, 3 = History, 4 = Settings
    var activeSubScreen by remember { mutableIntStateOf(1) } // Default to Home/Create after splash checked
    var showSplashIntro by remember { mutableStateOf(false) }

    // Input States
    var selectedImageName by remember { mutableStateOf("sample_city_skyline.jpg") }
    var customPrompt by remember { mutableStateOf("An uncanny alternate reality with subtle historical glitches and digital matrix rain") }
    var selectedMode by remember { mutableStateOf("Dual") } // "Mandela", "Matrix", or "Dual"
    var realityStrength by remember { mutableFloatStateOf(0.75f) }

    // Generation States
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var currentGlitchResult by remember { mutableStateOf<GlitchHistoryItem?>(null) }

    // History items state
    val glitchHistory = remember {
        mutableStateListOf(
            GlitchHistoryItem(
                id = "glitch_101",
                timestamp = System.currentTimeMillis() - 3600000,
                sourceImageName = "berenstein_bears_cover.png",
                prompt = "Berenstain vs Berenstein logo discrepancy in nostalgic amber film tint",
                mode = "Mandela",
                realityStrength = 0.85f,
                isFavorite = true
            ),
            GlitchHistoryItem(
                id = "glitch_102",
                timestamp = System.currentTimeMillis() - 7200000,
                sourceImageName = "monopoly_man_monocle.jpg",
                prompt = "Monopoly man with monocle glitch cascading in green matrix code stream",
                mode = "Matrix",
                realityStrength = 0.90f,
                isFavorite = false
            ),
            GlitchHistoryItem(
                id = "glitch_103",
                timestamp = System.currentTimeMillis() - 14400000,
                sourceImageName = "pikachu_tail_tip.png",
                prompt = "Black tip Pikachu tail + falling matrix glyphs overlay",
                mode = "Dual",
                realityStrength = 0.70f,
                isFavorite = true
            )
        )
    }

    // Settings States
    var apiKeyInput by remember { mutableStateOf("") }
    var activeTheme by remember { mutableStateOf("Dark Matrix Cyber-Green") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mandela_core_module"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // App Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mandela vs Matrix Re-imagenator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FF66) // Matrix Neon Green Accent
                    )
                    Text(
                        text = "Parallel Reality & Digital Glitch Generator",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFB74D) // Mandela Warm Amber Accent
                    )
                }

                OutlinedButton(
                    onClick = { showSplashIntro = !showSplashIntro },
                    modifier = Modifier.testTag("btn_toggle_splash_intro")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Intro",
                        tint = Color(0xFF00FF66)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (showSplashIntro) "Hide Intro" else "Splash Intro", fontSize = 11.sp)
                }
            }
        }

        // Sub Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = activeSubScreen,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            edgePadding = 8.dp
        ) {
            Tab(
                selected = activeSubScreen == 0,
                onClick = { activeSubScreen = 0 },
                modifier = Modifier.testTag("tab_splash"),
                text = { Text("Intro & Lore", fontSize = 12.sp) }
            )
            Tab(
                selected = activeSubScreen == 1,
                onClick = { activeSubScreen = 1 },
                modifier = Modifier.testTag("tab_home_create"),
                text = { Text("Home / Create", fontSize = 12.sp) }
            )
            Tab(
                selected = activeSubScreen == 2,
                onClick = { activeSubScreen = 2 },
                modifier = Modifier.testTag("tab_result"),
                text = { Text("Glitch Comparison", fontSize = 12.sp) }
            )
            Tab(
                selected = activeSubScreen == 3,
                onClick = { activeSubScreen = 3 },
                modifier = Modifier.testTag("tab_history"),
                text = { Text("History (${glitchHistory.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = activeSubScreen == 4,
                onClick = { activeSubScreen = 4 },
                modifier = Modifier.testTag("tab_settings"),
                text = { Text("Settings & State", fontSize = 12.sp) }
            )
        }

        // Main Content Container based on SubScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (activeSubScreen) {
                0 -> {
                    // SCREEN 1: SPLASH / ONBOARDING LORE
                    SplashOnboardingScreen(
                        onStartCreating = { activeSubScreen = 1 }
                    )
                }
                1 -> {
                    // SCREEN 2: HOME / CREATE STUDIO
                    HomeCreateStudioScreen(
                        selectedImageName = selectedImageName,
                        customPrompt = customPrompt,
                        selectedMode = selectedMode,
                        realityStrength = realityStrength,
                        isGenerating = isGenerating,
                        generationProgress = generationProgress,
                        onImageSelected = { selectedImageName = it },
                        onPromptChanged = { customPrompt = it },
                        onModeSelected = { selectedMode = it },
                        onStrengthChanged = { realityStrength = it },
                        onReimagineTriggered = {
                            isGenerating = true
                            generationProgress = 0.1f
                            scope.launch {
                                delay(400)
                                generationProgress = 0.4f
                                delay(500)
                                generationProgress = 0.8f
                                delay(400)
                                generationProgress = 1.0f
                                isGenerating = false

                                val newItem = GlitchHistoryItem(
                                    id = "glitch_${System.currentTimeMillis() / 1000}",
                                    timestamp = System.currentTimeMillis(),
                                    sourceImageName = selectedImageName,
                                    prompt = customPrompt,
                                    mode = selectedMode,
                                    realityStrength = realityStrength,
                                    isFavorite = false
                                )
                                glitchHistory.add(0, newItem)
                                currentGlitchResult = newItem
                                activeSubScreen = 2
                                Toast.makeText(context, "Dual Reality Glitch Generated!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                2 -> {
                    // SCREEN 3: GENERATION RESULT COMPARISON VIEW
                    GenerationResultScreen(
                        glitchItem = currentGlitchResult ?: glitchHistory.firstOrNull() ?: GlitchHistoryItem(
                            id = "demo_glitch",
                            timestamp = System.currentTimeMillis(),
                            sourceImageName = "sample_city_skyline.jpg",
                            prompt = "Matrix code rain + Mandela false memory shift",
                            mode = "Dual",
                            realityStrength = 0.80f
                        ),
                        onStrengthUpdated = { newStrength ->
                            currentGlitchResult = currentGlitchResult?.copy(realityStrength = newStrength)
                        },
                        onToggleFavorite = { item ->
                            val index = glitchHistory.indexOfFirst { it.id == item.id }
                            if (index != -1) {
                                glitchHistory[index] = item.copy(isFavorite = !item.isFavorite)
                                currentGlitchResult = glitchHistory[index]
                            }
                        },
                        onRegenerateStronger = {
                            isGenerating = true
                            scope.launch {
                                delay(1000)
                                isGenerating = false
                                realityStrength = (realityStrength + 0.15f).coerceAtMost(1.0f)
                                currentGlitchResult = currentGlitchResult?.copy(realityStrength = realityStrength)
                                Toast.makeText(context, "Regenerated with Extreme Reality Glitch!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                3 -> {
                    // SCREEN 4: HISTORY / GALLERY
                    HistoryGalleryScreen(
                        historyList = glitchHistory,
                        onSelectItem = { item ->
                            currentGlitchResult = item
                            activeSubScreen = 2
                        },
                        onDeleteItem = { item ->
                            glitchHistory.remove(item)
                            Toast.makeText(context, "Glitch record deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                4 -> {
                    // SCREEN 5: SETTINGS & QUANTUM STATE ENGINE
                    SettingsAndStateScreen(
                        apiKeyInput = apiKeyInput,
                        activeTheme = activeTheme,
                        quantumNodes = quantumNodes,
                        onApiKeyChanged = { apiKeyInput = it },
                        onThemeChanged = { activeTheme = it },
                        onCycleQuantumState = onCycleState,
                        onClearCache = {
                            Toast.makeText(context, "Glitch cache and memory buffers cleared", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

// ==================== SCREEN 1: SPLASH / ONBOARDING ====================
@Composable
private fun SplashOnboardingScreen(
    onStartCreating: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1710)), // Dark Matrix Green Tint
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF003311)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Glitch Core",
                        tint = Color(0xFF00FF66),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "MANDELA vs MATRIX",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00FF66),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "\"Did you remember it wrong... or was the code updated?\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFFB74D),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF050B06), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "01001000 01100101 01101100 01101100 01101111",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00FF66).copy(alpha = 0.7f)
                        )
                        Text(
                            text = "1. Mandela Effect Mode: Alters history, logos, film scenes, and colors using warm nostalgic film grain and false memory shift.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "2. Matrix Mode: Overlays falling green rain code, simulation glitches, cyberpunk cascades, and red/blue pill dualism.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Button(
                    onClick = onStartCreating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66), contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_enter_reimagenator_studio")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enter Re-imagenator Studio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== SCREEN 2: HOME / CREATE ====================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeCreateStudioScreen(
    selectedImageName: String,
    customPrompt: String,
    selectedMode: String,
    realityStrength: Float,
    isGenerating: Boolean,
    generationProgress: Float,
    onImageSelected: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onModeSelected: (String) -> Unit,
    onStrengthChanged: (Float) -> Unit,
    onReimagineTriggered: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Select or Capture Photo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Source Image Selection",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onImageSelected("captured_photo_${System.currentTimeMillis() / 1000}.jpg")
                            Toast.makeText(context, "Captured camera snapshot", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_camera_capture")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Camera")
                    }

                    OutlinedButton(
                        onClick = {
                            onImageSelected("gallery_pick_${System.currentTimeMillis() / 1000}.jpg")
                            Toast.makeText(context, "Selected photo from Gallery", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_gallery_pick")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Gallery")
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Selected: $selectedImageName",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Step 2: Custom Reality Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "2. Reality Instruction Prompt",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = customPrompt,
                    onValueChange = onPromptChanged,
                    label = { Text("Describe Glitch / Reality Modification") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_prompt"),
                    singleLine = false,
                    maxLines = 3
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { onPromptChanged("Berenstain vs Berenstein logo shift with false memory tint") },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text("Berenstain Logo", fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                    }

                    Surface(
                        onClick = { onPromptChanged("Matrix digital rain code stream overlay with glowing neon glyphs") },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text("Matrix Rain Code", fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                    }

                    Surface(
                        onClick = { onPromptChanged("Monopoly monocle false memory + cyberpunk red/blue pill dualism") },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text("Monopoly Monocle", fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }

        // Step 3: Style Mode & Reality Strength Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "3. Reality Mode & Glitch Strength",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Style Mode Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StyleToggleCard(
                        title = "Mandela Effect",
                        subtitle = "Warm Nostalgic Glitch",
                        isSelected = selectedMode == "Mandela",
                        accentColor = Color(0xFFFFB74D),
                        onClick = { onModeSelected("Mandela") },
                        modifier = Modifier.weight(1f)
                    )

                    StyleToggleCard(
                        title = "Matrix Mode",
                        subtitle = "Digital Rain Stream",
                        isSelected = selectedMode == "Matrix",
                        accentColor = Color(0xFF00FF66),
                        onClick = { onModeSelected("Matrix") },
                        modifier = Modifier.weight(1f)
                    )

                    StyleToggleCard(
                        title = "Dual Mode",
                        subtitle = "Both Realities",
                        isSelected = selectedMode == "Dual",
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = { onModeSelected("Dual") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Reality Strength Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Reality Strength (Glitch Level):", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${(realityStrength * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = realityStrength,
                        onValueChange = onStrengthChanged,
                        valueRange = 0.1f..1.0f,
                        modifier = Modifier.testTag("slider_reality_strength")
                    )
                }
            }
        }

        // Re-Imagine Action Button
        Button(
            onClick = onReimagineTriggered,
            enabled = !isGenerating,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00FF66),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_reimagine_dual_reality")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("Re-imagining Alternate Realities...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Re-Imagine (Generate Dual Glitch)", fontWeight = FontWeight.Bold)
            }
        }

        if (isGenerating) {
            LinearProgressIndicator(
                progress = { generationProgress },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF00FF66)
            )
        }
    }
}

@Composable
private fun StyleToggleCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, accentColor) else null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== SCREEN 3: GENERATION RESULT COMPARISON ====================
@Composable
private fun GenerationResultScreen(
    glitchItem: GlitchHistoryItem,
    onStrengthUpdated: (Float) -> Unit,
    onToggleFavorite: (GlitchHistoryItem) -> Unit,
    onRegenerateStronger: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Alternate Reality Comparison",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Glitch ID: ${glitchItem.id} • Strength: ${(glitchItem.realityStrength * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { onToggleFavorite(glitchItem) }) {
                        Icon(
                            imageVector = if (glitchItem.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (glitchItem.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Side-By-Side Comparison Panels
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Original Photo Mock Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color(0xFF1E293B), shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column {
                            Text("ORIGINAL TIMELINE", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text("Image: ${glitchItem.sourceImageName}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }

                    // Mandela Effect Result Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF3E2723), Color(0xFF5D4037))
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("MANDELA EFFECT REALITY", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Surface(color = Color(0xFFFFB74D).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                    Text("Nostalgic Film Tint", fontSize = 9.sp, modifier = Modifier.padding(2.dp), color = Color(0xFFFFB74D))
                                }
                            }
                            Text("Shifted details: ${glitchItem.prompt}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }

                    // Matrix Mode Result Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF002200), Color(0xFF004411))
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("MATRIX DIGITAL RAIN REALITY", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00FF66), fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Surface(color = Color(0xFF00FF66).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                    Text("24FPS Matrix Cascade", fontSize = 9.sp, modifier = Modifier.padding(2.dp), color = Color(0xFF00FF66))
                                }
                            }
                            Text("Matrix Glitch Stream: 01001001 01101110 01100110 01101111", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = Color(0xFF00FF66))
                        }
                    }
                }

                // Adjust Reality Strength Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Live Adjust Reality Glitch Intensity:", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = glitchItem.realityStrength,
                        onValueChange = onStrengthUpdated,
                        valueRange = 0.1f..1.0f
                    )
                }

                // Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { Toast.makeText(context, "Exported both alternate reality images to Gallery", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Save")
                    }

                    OutlinedButton(
                        onClick = { Toast.makeText(context, "Glitch share link generated!", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onRegenerateStronger,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66), contentColor = Color.Black),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Stronger Glitch", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 4: HISTORY / GALLERY ====================
@Composable
private fun HistoryGalleryScreen(
    historyList: List<GlitchHistoryItem>,
    onSelectItem: (GlitchHistoryItem) -> Unit,
    onDeleteItem: (GlitchHistoryItem) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Mandela", "Matrix", "Dual"

    val filteredList = historyList.filter {
        when (selectedFilter) {
            "Mandela" -> it.mode == "Mandela"
            "Matrix" -> it.mode == "Matrix"
            "Dual" -> it.mode == "Dual"
            else -> true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Glitch History Vault",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                FilterChip(
                    selected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    label = { Text("All", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "Mandela",
                    onClick = { selectedFilter = "Mandela" },
                    label = { Text("Mandela", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "Matrix",
                    onClick = { selectedFilter = "Matrix" },
                    label = { Text("Matrix", fontSize = 11.sp) }
                )
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No glitches detected yet... reality is currently stable.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredList.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectItem(item) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.mode + " Glitch",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.mode == "Matrix") Color(0xFF00FF66) else Color(0xFFFFB74D)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "${(item.realityStrength * 100).toInt()}% Intensity",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = item.prompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }

                            Row {
                                IconButton(onClick = { onSelectItem(item) }) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "View")
                                }
                                IconButton(onClick = { onDeleteItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== SCREEN 5: SETTINGS & QUANTUM STATE ENGINE ====================
@Composable
private fun SettingsAndStateScreen(
    apiKeyInput: String,
    activeTheme: String,
    quantumNodes: List<Pair<String, String>>,
    onApiKeyChanged: (String) -> Unit,
    onThemeChanged: (String) -> Unit,
    onCycleQuantumState: () -> Unit,
    onClearCache: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // API Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("AI Provider API Configuration", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = onApiKeyChanged,
                    label = { Text("Hugging Face / Replicate / Gemini API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Current Theme:", style = MaterialTheme.typography.bodySmall)
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(4.dp)) {
                        Text(activeTheme, fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                    }
                }

                OutlinedButton(
                    onClick = onClearCache,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Clear Glitch Buffer & Image Cache")
                }
            }
        }

        // Quantum State Inspector (From Original MandelaCore)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Mandela Quantum Heap Matrix", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    Button(onClick = onCycleQuantumState) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cycle States")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quantumNodes.chunked(2).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { (nodeName, valStr) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(nodeName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        Text(valStr, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
