package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class OpenRouterModel(
    val id: String,
    val name: String,
    val description: String,
    val isFree: Boolean = true
)

object OpenRouterApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val POPULAR_MODELS = listOf(
        OpenRouterModel("meta-llama/llama-3.2-3b-instruct:free", "Meta Llama 3.2 3B (Free)", "Fast lightweight open model"),
        OpenRouterModel("google/gemma-2-9b-it:free", "Google Gemma 2 9B (Free)", "High performance open model from Google"),
        OpenRouterModel("qwen/qwen-2.5-coder-32b-instruct:free", "Qwen 2.5 Coder 32B (Free)", "State-of-the-art open coding assistant"),
        OpenRouterModel("deepseek/deepseek-r1:free", "DeepSeek R1 (Free)", "Reasoning model for complex logic"),
        OpenRouterModel("mistralai/mistral-7b-instruct:free", "Mistral 7B Instruct (Free)", "Versatile open NLP model")
    )

    /**
     * Executes a chat completion query to OpenRouter API.
     */
    suspend fun chatCompletion(
        prompt: String,
        apiKey: String,
        modelId: String = "meta-llama/llama-3.2-3b-instruct:free",
        systemPrompt: String = "You are an expert AI assistant integrated in an Android application."
    ): String = withContext(Dispatchers.IO) {
        val key = apiKey.trim()
        if (key.isBlank()) {
            return@withContext "OpenRouter Error: Missing API Key. Please enter your OpenRouter API Key in settings or header config."
        }

        val url = "https://openrouter.ai/api/v1/chat/completions"
        val jsonPayload = JSONObject().apply {
            put("model", modelId)
            put("messages", JSONArray().apply {
                if (systemPrompt.isNotBlank()) {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                }
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 1024)
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer $key")
            .header("HTTP-Referer", "https://ai.studio/build")
            .header("X-Title", "Android Builder OpenRouter Client")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext "OpenRouter API Error [${response.code}]: $bodyStr"
                }

                val jsonResp = JSONObject(bodyStr)
                val choices = jsonResp.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val messageObj = choices.getJSONObject(0).optJSONObject("message")
                    val content = messageObj?.optString("content", "")
                    if (!content.isNullOrBlank()) {
                        return@withContext content
                    }
                }

                "Response received from OpenRouter ($modelId):\n$bodyStr"
            }
        } catch (e: Exception) {
            "OpenRouter Connection Error: ${e.localizedMessage ?: e.message}"
        }
    }
}
