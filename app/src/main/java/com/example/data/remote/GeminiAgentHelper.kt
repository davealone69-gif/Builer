package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper class for interacting with Gemini AI Agent services,
 * managing model selection, prompt construction, system instructions,
 * and code snippet extraction for Android build tasks.
 */
object GeminiAgentHelper {

    // Standard supported Gemini model identifiers
    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_IMAGE = "gemini-2.5-flash-image"

    /**
     * System instructions for AI Agent roles
     */
    enum class AgentRole(val systemPrompt: String) {
        ANDROID_ARCHITECT(
            "You are an expert Android Architect specializing in Jetpack Compose, Kotlin Coroutines, " +
            "StateFlow, Room Database, clean architecture MVVM, and Material Design 3 guidelines."
        ),
        SYSTEM_DESIGNER(
            "You are a Senior System Design Architect. Provide scalable, high-performance distributed " +
            "system architecture diagrams, database schemas, and microservice patterns."
        ),
        MLOPS_SPECIALIST(
            "You are an MLOps & Machine Learning Engineer specializing in ZenML, model registries, " +
            "feature stores, and production ML pipelines."
        ),
        CODE_AUDITOR(
            "You are a Principal Security & Code Quality Auditor for Android applications. " +
            "Analyze code for vulnerabilities, memory leaks, performance bottlenecks, and architectural issues."
        )
    }

    /**
     * Formats a prompt with role-specific system instructions and context.
     */
    fun buildAgentPrompt(
        userPrompt: String,
        role: AgentRole = AgentRole.ANDROID_ARCHITECT,
        additionalContext: String? = null
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Role Instruction: ${role.systemPrompt}")
        if (!additionalContext.isNullOrBlank()) {
            sb.appendLine("\nProject Context:\n$additionalContext")
        }
        sb.appendLine("\nUser Task:\n$userPrompt")
        sb.appendLine("\nProvide complete, production-ready code or structured details without placeholders or truncation.")
        return sb.toString()
    }

    /**
     * Executes a prompt through Gemini API with the specified role and optional custom API key.
     */
    suspend fun queryGeminiAgent(
        prompt: String,
        role: AgentRole = AgentRole.ANDROID_ARCHITECT,
        customApiKey: String? = null,
        modelName: String = MODEL_FLASH
    ): String = withContext(Dispatchers.IO) {
        val fullPrompt = buildAgentPrompt(prompt, role)
        GeminiRestApi.generateContent(
            prompt = fullPrompt,
            customApiKey = customApiKey,
            modelName = modelName
        )
    }

    /**
     * Extracts pure Kotlin / Markdown code blocks from a Gemini response string.
     */
    fun extractCodeFromResponse(response: String, language: String = "kotlin"): String {
        val regex = "```(?:$language)?\\s*([\\s\\S]*?)\\s*```".toRegex(RegexOption.IGNORE_CASE)
        val match = regex.find(response)
        return match?.groups?.get(1)?.value?.trim() ?: response.trim()
    }

    /**
     * Checks whether the Gemini API Key is configured in BuildConfig or environment.
     */
    fun isApiKeyConfigured(customApiKey: String? = null): Boolean {
        if (!customApiKey.isNullOrBlank() && customApiKey != "MY_GEMINI_API_KEY") {
            return true
        }
        val defaultKey = runCatching {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String
        }.getOrNull()
        return !defaultKey.isNullOrBlank() && defaultKey != "MY_GEMINI_API_KEY"
    }
}
