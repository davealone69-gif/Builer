package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class OllamaModel(
    val name: String,
    val description: String
)

object OllamaApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val POPULAR_OLLAMA_MODELS = listOf(
        OllamaModel("llama3.2", "Meta Llama 3.2 1B / 3B lightweight local model"),
        OllamaModel("mistral", "Mistral 7B general reasoning model"),
        OllamaModel("gemma2", "Google Gemma 2 high accuracy local model"),
        OllamaModel("qwen2.5-coder", "Qwen 2.5 specialized code model"),
        OllamaModel("deepseek-r1:8b", "DeepSeek R1 8B distilled local reasoning model"),
        OllamaModel("phi3", "Microsoft Phi-3 mini 3.8B model")
    )

    /**
     * Executes a prompt generation request on an Ollama server instance.
     */
    suspend fun generateResponse(
        hostUrl: String = "http://10.0.2.2:11434",
        apiKey: String = "",
        model: String = "llama3.2",
        prompt: String
    ): String = withContext(Dispatchers.IO) {
        val sanitizedHost = hostUrl.trim().trimEnd('/')
        if (sanitizedHost.isBlank()) {
            return@withContext "Ollama Error: Host URL cannot be blank."
        }

        val endpoint = "$sanitizedHost/api/generate"
        val jsonPayload = JSONObject().apply {
            put("model", model)
            put("prompt", prompt)
            put("stream", false)
        }

        val requestBuilder = Request.Builder()
            .url(endpoint)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))

        val key = apiKey.trim()
        if (key.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer $key")
            requestBuilder.header("X-API-KEY", key)
        }

        try {
            client.newCall(requestBuilder.build()).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext "Ollama Error [${response.code}]: $bodyStr"
                }

                val jsonObj = JSONObject(bodyStr)
                val responseText = jsonObj.optString("response", "")
                if (responseText.isNotBlank()) {
                    return@withContext responseText
                }

                "Ollama Output ($model):\n$bodyStr"
            }
        } catch (e: Exception) {
            "Ollama Connection Error: Failed to connect to $sanitizedHost. Ensure your Ollama server is running. (${e.localizedMessage ?: e.message})"
        }
    }
}
