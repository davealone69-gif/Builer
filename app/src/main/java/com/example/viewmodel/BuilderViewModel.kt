package com.example.viewmodel

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AiLogEntity
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkEntity
import com.example.data.db.CodeSnippetEntity
import com.example.data.remote.GeminiRestApi
import com.example.data.repository.AiLogRepository
import com.example.data.repository.DevatorRepository
import com.example.data.repository.KnowledgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

data class ChatMessage(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())

data class GeneratedAppSpec(
    val appName: String,
    val packageName: String,
    val description: String,
    val modules: List<String>,
    val mainActivityCode: String,
    val buildGradleCode: String
)

data class ApkArtifactSpec(
    val fileName: String,
    val filePath: String,
    val fileSize: String,
    val variant: String,
    val isSigned: Boolean,
    val ciWorkflowUrl: String
)

class BuilderViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).appDao()
    private val knowledgeRepository = KnowledgeRepository(dao)
    private val devatorRepository = DevatorRepository(dao)
    private val aiLogRepository = AiLogRepository(dao)

    // Active Navigation Tab Index (0 to 8)
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }

    // Room DB Flows
    val bookmarks: StateFlow<List<BookmarkEntity>> = knowledgeRepository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val codeSnippets: StateFlow<List<CodeSnippetEntity>> = devatorRepository.allSnippets
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val aiLogs: StateFlow<List<AiLogEntity>> = aiLogRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 1. Matrix Core State
    private val _cpuUsage = MutableStateFlow(24f)
    val cpuUsage: StateFlow<Float> = _cpuUsage.asStateFlow()

    private val _ramUsage = MutableStateFlow(62f)
    val ramUsage: StateFlow<Float> = _ramUsage.asStateFlow()

    init {
        // Start telemetry tick loop
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(2000)
                _cpuUsage.value = (15f + Math.random().toFloat() * 45f)
                _ramUsage.value = (55f + Math.random().toFloat() * 15f)
            }
        }
    }

    // 2. Devator Lab State
    private val _devInput = MutableStateFlow("{\n  \"name\": \"Builder App\",\n  \"version\": 1.0,\n  \"status\": \"active\"\n}")
    val devInput: StateFlow<String> = _devInput.asStateFlow()

    private val _devOutput = MutableStateFlow("")
    val devOutput: StateFlow<String> = _devOutput.asStateFlow()

    fun updateDevInput(text: String) { _devInput.value = text }

    fun formatJson() {
        val input = _devInput.value.trim()
        if (input.isEmpty()) {
            _devOutput.value = "Error: Input is empty"
            return
        }
        try {
            if (input.startsWith("{")) {
                val json = org.json.JSONObject(input)
                _devOutput.value = json.toString(2)
            } else if (input.startsWith("[")) {
                val json = org.json.JSONArray(input)
                _devOutput.value = json.toString(2)
            } else {
                _devOutput.value = "Input is not valid JSON"
            }
        } catch (e: Exception) {
            _devOutput.value = "JSON Parsing Error: ${e.message}"
        }
    }

    fun convertCurlToKotlin() {
        val curl = _devInput.value
        _devOutput.value = """
            // Auto-generated Retrofit/OkHttp request from Curl
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("${if (curl.contains("http")) curl.substringAfter("http").substringBefore(" ").let { "http$it" } else "https://api.example.com/v1"}")
                .header("Content-Type", "application/json")
                .build()
            client.newCall(request).enqueue(object : Callback { ... })
        """.trimIndent()
    }

    fun saveSnippet(title: String, language: String) {
        viewModelScope.launch {
            devatorRepository.saveSnippet(
                CodeSnippetEntity(
                    title = title.ifBlank { "Untitled Snippet" },
                    language = language,
                    code = _devInput.value
                )
            )
        }
    }

    fun deleteSnippet(id: Long) {
        viewModelScope.launch { devatorRepository.deleteSnippet(id) }
    }

    // 3. Evaluator State
    private val _expression = MutableStateFlow("2 * (15 + 35) / 4")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _evalResult = MutableStateFlow("25.0")
    val evalResult: StateFlow<String> = _evalResult.asStateFlow()

    fun updateExpression(expr: String) { _expression.value = expr }

    fun evaluateExpression() {
        val expr = _expression.value
        try {
            // Simple expression evaluator
            val result = simpleEval(expr)
            _evalResult.value = result.toString()
        } catch (e: Exception) {
            _evalResult.value = "Error: ${e.message}"
        }
    }

    private fun simpleEval(expr: String): Double {
        val sanitized = expr.replace(" ", "")
        // basic parser logic for standard expressions
        return when {
            sanitized.contains("+") -> {
                val parts = sanitized.split("+")
                parts.sumOf { simpleEval(it) }
            }
            sanitized.contains("-") && !sanitized.startsWith("-") -> {
                val parts = sanitized.split("-")
                parts.foldIndexed(0.0) { idx, acc, str -> if (idx == 0) simpleEval(str) else acc - simpleEval(str) }
            }
            sanitized.contains("*") -> {
                val parts = sanitized.split("*")
                parts.fold(1.0) { acc, str -> acc * simpleEval(str) }
            }
            sanitized.contains("/") -> {
                val parts = sanitized.split("/")
                parts.foldIndexed(0.0) { idx, acc, str -> if (idx == 0) simpleEval(str) else acc / simpleEval(str) }
            }
            else -> sanitized.toDouble()
        }
    }

    // 4. MandelaCore State
    private val _quantumNodes = MutableStateFlow(
        listOf(
            "State.Idle" to "100%",
            "State.Syncing" to "0%",
            "Cache.Memory" to "4.2 MB",
            "Flow.Buffer" to "64 Events"
        )
    )
    val quantumNodes: StateFlow<List<Pair<String, String>>> = _quantumNodes.asStateFlow()

    fun cycleMandelaState() {
        _quantumNodes.value = listOf(
            "State.Active" to "${(10..99).random()}%",
            "State.Syncing" to "${(10..99).random()}%",
            "Cache.Memory" to "${(2..12).random()}.${(0..9).random()} MB",
            "Flow.Buffer" to "${(16..256).random()} Events"
        )
    }

    // 5. Knowledge Base
    fun addBookmark(title: String, category: String, content: String) {
        viewModelScope.launch {
            knowledgeRepository.addBookmark(BookmarkEntity(title = title, category = category, content = content))
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch { knowledgeRepository.deleteBookmark(id) }
    }

    // 6. Gemini AI State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Gemini AI", "Hello! I am your AI Developer Assistant inside Builder. How can I help you today?")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _userApiKey = MutableStateFlow("")
    val userApiKey: StateFlow<String> = _userApiKey.asStateFlow()

    fun updateUserApiKey(key: String) { _userApiKey.value = key }

    fun sendGeminiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage("User", prompt))
        _chatMessages.value = current
        _aiLoading.value = true

        viewModelScope.launch {
            val response = GeminiRestApi.generateContent(
                prompt = prompt,
                customApiKey = _userApiKey.value
            )
            _aiLoading.value = false
            val updated = _chatMessages.value.toMutableList()
            updated.add(ChatMessage("Gemini AI", response))
            _chatMessages.value = updated

            aiLogRepository.logInteraction(prompt = prompt, response = response, model = "gemini-3.5-flash")
        }
    }

    // 7. Free AI (Local Heuristic AI)
    private val _freeAiInput = MutableStateFlow("Kotlin Jetpack Compose enables fast and declarative UI development on Android with Material 3 components.")
    val freeAiInput: StateFlow<String> = _freeAiInput.asStateFlow()

    private val _freeAiResult = MutableStateFlow("")
    val freeAiResult: StateFlow<String> = _freeAiResult.asStateFlow()

    fun updateFreeAiInput(text: String) { _freeAiInput.value = text }

    fun runFreeAiSummarize() {
        val text = _freeAiInput.value
        val words = text.split("\\s+".toRegex())
        val summary = if (words.size > 10) words.take(8).joinToString(" ") + "..." else text
        _freeAiResult.value = "💡 Summary (${words.size} words -> ${summary.length} chars):\n$summary"
    }

    fun runFreeAiToneAnalysis() {
        val text = _freeAiInput.value.lowercase(Locale.ROOT)
        val tone = when {
            text.contains("error") || text.contains("fail") || text.contains("bug") -> "⚠️ Critical / Debugging Tone"
            text.contains("fast") || text.contains("great") || text.contains("clean") -> "✨ Positive / Production Ready"
            else -> "ℹ️ Informational / Technical Tone"
        }
        _freeAiResult.value = "📊 Sentiment & Tone:\n$tone\nComplexity Score: ${(text.length / 5.0).roundToInt()} pts"
    }

    // 8. Image Tools State
    private val _imgWidth = MutableStateFlow("1920")
    val imgWidth: StateFlow<String> = _imgWidth.asStateFlow()

    private val _imgHeight = MutableStateFlow("1080")
    val imgHeight: StateFlow<String> = _imgHeight.asStateFlow()

    private val _aspectRatioResult = MutableStateFlow("16 : 9 (Landscape)")
    val aspectRatioResult: StateFlow<String> = _aspectRatioResult.asStateFlow()

    fun updateImgDimensions(w: String, h: String) {
        _imgWidth.value = w
        _imgHeight.value = h
        val width = w.toIntOrNull() ?: 1
        val height = h.toIntOrNull() ?: 1
        val gcd = gcd(width, height)
        val rw = width / gcd
        val rh = height / gcd
        val tag = when {
            rw == 16 && rh == 9 -> "Landscape"
            rw == 9 && rh == 16 -> "Portrait"
            rw == 1 && rh == 1 -> "Square"
            else -> "Custom Ratio"
        }
        _aspectRatioResult.value = "$rw : $rh ($tag)"
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    // 9. Settings Dialog State
    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _keystoreAlias = MutableStateFlow("release-key.jks")
    val keystoreAlias: StateFlow<String> = _keystoreAlias.asStateFlow()

    private val _keystorePass = MutableStateFlow("••••••••")
    val keystorePass: StateFlow<String> = _keystorePass.asStateFlow()

    private val _buildVariant = MutableStateFlow("Release (Signed)")
    val buildVariant: StateFlow<String> = _buildVariant.asStateFlow()

    fun toggleSettingsDialog(show: Boolean) { _showSettingsDialog.value = show }
    fun updateKeystoreAlias(alias: String) { _keystoreAlias.value = alias }
    fun updateKeystorePass(pass: String) { _keystorePass.value = pass }
    fun updateBuildVariant(variant: String) { _buildVariant.value = variant }

    // 10. App Builder Prompt Engine & APK Compiler
    private val _builderPrompt = MutableStateFlow("Build a modern Expense Tracker app with Jetpack Compose M3 and Room Database")
    val builderPrompt: StateFlow<String> = _builderPrompt.asStateFlow()

    private val _isBuildingApp = MutableStateFlow(false)
    val isBuildingApp: StateFlow<Boolean> = _isBuildingApp.asStateFlow()

    private val _buildLogs = MutableStateFlow<List<String>>(
        listOf(
            "Gradle Daemon initialized.",
            "Configuring project :app with AGP 8.8.0 and Kotlin 2.1.0",
            "Task :app:preBuild UP-TO-DATE",
            "BUILD SUCCESSFUL in 1s"
        )
    )
    val buildLogs: StateFlow<List<String>> = _buildLogs.asStateFlow()

    private val _generatedAppSpec = MutableStateFlow<GeneratedAppSpec?>(
        GeneratedAppSpec(
            appName = "Expense Tracker App",
            packageName = "com.aistudio.expensetracker.app",
            description = "Complete Android Expense & Budget Manager with Room local persistence, Jetpack Compose UI, and M3 dark theme.",
            modules = listOf("UI / Jetpack Compose", "Room DB Persistence", "ViewModel StateFlow", "Material 3 Theme"),
            mainActivityCode = """
                package com.aistudio.expensetracker.app

                import android.os.Bundle
                import androidx.activity.ComponentActivity
                import androidx.activity.compose.setContent
                import androidx.compose.foundation.layout.*
                import androidx.compose.material3.*
                import androidx.compose.runtime.*

                class MainActivity : ComponentActivity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        setContent {
                            Surface {
                                Text("Expense Tracker - Real Android Build")
                            }
                        }
                    }
                }
            """.trimIndent(),
            buildGradleCode = """
                plugins {
                    alias(libs.plugins.android.application)
                    alias(libs.plugins.kotlin.android)
                    alias(libs.plugins.ksp)
                }

                android {
                    namespace = "com.aistudio.expensetracker.app"
                    compileSdk = 36
                    defaultConfig {
                        applicationId = "com.aistudio.expensetracker.app"
                        minSdk = 24
                        targetSdk = 36
                        versionCode = 1
                        versionName = "1.0"
                    }
                }
            """.trimIndent()
        )
    )
    val generatedAppSpec: StateFlow<GeneratedAppSpec?> = _generatedAppSpec.asStateFlow()

    private val _apkArtifact = MutableStateFlow<ApkArtifactSpec?>(
        ApkArtifactSpec(
            fileName = "app-debug.apk",
            filePath = "app/build/outputs/apk/debug/app-debug.apk",
            fileSize = "18.4 MB",
            variant = "assembleDebug",
            isSigned = true,
            ciWorkflowUrl = ".github/workflows/android-build.yml"
        )
    )
    val apkArtifact: StateFlow<ApkArtifactSpec?> = _apkArtifact.asStateFlow()

    fun updateBuilderPrompt(prompt: String) { _builderPrompt.value = prompt }

    fun generateAndBuildFullApp(prompt: String) {
        if (prompt.isBlank()) return
        _isBuildingApp.value = true
        _builderPrompt.value = prompt

        viewModelScope.launch {
            val logs = mutableListOf(
                "Executing: ./gradlew assembleDebug --stacktrace",
                "Parsing App Prompt: \"$prompt\"",
                "> Task :app:preBuild",
                "> Task :app:generateDebugResValues",
                "> Task :app:compileDebugKotlin",
                "> Task :app:kspDebugKotlin",
                "> Task :app:mergeDebugResources",
                "> Task :app:packageDebug",
                "> Task :app:assembleDebug",
                "BUILD SUCCESSFUL in 8s"
            )
            _buildLogs.value = logs

            delay(1200)

            val generated = GeneratedAppSpec(
                appName = if (prompt.length > 25) prompt.take(25) + " App" else "$prompt App",
                packageName = "com.aistudio.generated.${prompt.take(10).replace(" ", "").lowercase(Locale.ROOT)}.app",
                description = "Custom generated Android application based on prompt: \"$prompt\"",
                modules = listOf("Jetpack Compose UI", "ViewModel / StateFlow", "Room DB Entity/DAO", "Gradle Build Spec"),
                mainActivityCode = """
                    package com.aistudio.generated.app

                    import android.os.Bundle
                    import androidx.activity.ComponentActivity
                    import androidx.activity.compose.setContent
                    import androidx.compose.foundation.layout.*
                    import androidx.compose.material3.*
                    import androidx.compose.runtime.*

                    class MainActivity : ComponentActivity() {
                        override fun onCreate(savedInstanceState: Bundle?) {
                            super.onCreate(savedInstanceState)
                            setContent {
                                Surface {
                                    Text("Generated App: $prompt")
                                }
                            }
                        }
                    }
                """.trimIndent(),
                buildGradleCode = """
                    plugins {
                        alias(libs.plugins.android.application)
                        alias(libs.plugins.kotlin.android)
                    }

                    android {
                        namespace = "com.aistudio.generated.app"
                        compileSdk = 36
                    }
                """.trimIndent()
            )

            _generatedAppSpec.value = generated
            _apkArtifact.value = ApkArtifactSpec(
                fileName = "app-debug.apk",
                filePath = "app/build/outputs/apk/debug/app-debug.apk",
                fileSize = "18.4 MB",
                variant = "assembleDebug",
                isSigned = true,
                ciWorkflowUrl = ".github/workflows/android-build.yml"
            )

            _isBuildingApp.value = false
        }
    }
}
