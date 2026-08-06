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

data class HuggingFaceModelInfo(
    val id: String,
    val pipelineTag: String,
    val likes: Int,
    val downloads: Int,
    val author: String
)

data class HuggingFaceHubItem(
    val id: String,
    val type: String, // "Model", "Dataset", "Space"
    val likes: Int,
    val downloadsOrViews: Int,
    val tag: String
)

object HuggingFaceApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val FEATURED_MODELS = listOf(
        "meta-llama/Llama-3.2-1B-Instruct" to "Meta Llama 3.2 1B (Fast General LLM)",
        "Qwen/Qwen2.5-Coder-7B-Instruct" to "Qwen 2.5 Coder 7B (State-of-the-Art Code Model)",
        "deepseek-ai/DeepSeek-R1-Distill-Qwen-32B" to "DeepSeek R1 Distill Qwen (Reasoning Engine)",
        "mistralai/Mistral-7B-Instruct-v0.3" to "Mistral 7B Instruct v0.3 (Versatile NLP)",
        "google/gemma-2-2b-it" to "Google Gemma 2 2B Instruct (Lightweight Google Model)"
    )

    /**
     * Queries Hugging Face Model Hub API for models matching a search query.
     */
    suspend fun searchModels(
        query: String,
        limit: Int = 10
    ): List<HuggingFaceModelInfo> = withContext(Dispatchers.IO) {
        val url = "https://huggingface.co/api/models?search=${query.trim()}&limit=$limit&sort=downloads&direction=-1"
        val request = Request.Builder()
            .url(url)
            .get()
            .header("User-Agent", "Android-Builder-App/1.1")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val responseStr = response.body?.string() ?: return@withContext emptyList()
                val jsonArray = JSONArray(responseStr)
                val results = mutableListOf<HuggingFaceModelInfo>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val id = item.optString("id", "")
                    val pipelineTag = item.optString("pipeline_tag", "text-generation")
                    val likes = item.optInt("likes", 0)
                    val downloads = item.optInt("downloads", 0)
                    val author = item.optString("author", id.substringBefore("/", "community"))

                    if (id.isNotBlank()) {
                        results.add(HuggingFaceModelInfo(id, pipelineTag, likes, downloads, author))
                    }
                }
                results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Executes text generation / chat inference on Hugging Face Serverless Inference API.
     */
    suspend fun runInference(
        prompt: String,
        modelId: String = "meta-llama/Llama-3.2-1B-Instruct",
        hfToken: String? = null,
        maxTokens: Int = 512
    ): String = withContext(Dispatchers.IO) {
        val token = hfToken?.trim()?.takeIf { it.isNotBlank() } ?: ""

        // Try OpenAI-compatible chat router endpoint first
        val routerUrl = "https://router.huggingface.co/hf-inference/v1/chat/completions"
        val jsonPayload = JSONObject().apply {
            put("model", modelId)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("max_tokens", maxTokens)
            put("temperature", 0.7)
        }

        val requestBuilder = Request.Builder()
            .url(routerUrl)
            .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))

        if (token.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        try {
            client.newCall(requestBuilder.build()).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (response.isSuccessful && bodyStr.isNotBlank()) {
                    val jsonResp = JSONObject(bodyStr)
                    val choices = jsonResp.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val firstMsg = choices.getJSONObject(0).optJSONObject("message")
                        val content = firstMsg?.optString("content", "")
                        if (!content.isNullOrBlank()) return@withContext content
                    }
                }
            }
        } catch (e: Exception) {
            // Fallthrough to standard inference endpoint
        }

        // Fallback to standard inference API: https://api-inference.huggingface.co/models/$modelId
        val directUrl = "https://api-inference.huggingface.co/models/$modelId"
        val directPayload = JSONObject().apply {
            put("inputs", prompt)
            put("parameters", JSONObject().apply {
                put("max_new_tokens", maxTokens)
                put("return_full_text", false)
            })
        }

        val directReqBuilder = Request.Builder()
            .url(directUrl)
            .post(directPayload.toString().toRequestBody("application/json".toMediaType()))

        if (token.isNotBlank()) {
            directReqBuilder.header("Authorization", "Bearer $token")
        }

        try {
            client.newCall(directReqBuilder.build()).execute().use { response ->
                val responseStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return@withContext "Hugging Face Inference Error [${response.code}]: $responseStr\n" +
                            "(Tip: Provide a free Hugging Face Access Token in settings if model rate limit is exceeded)"
                }

                if (responseStr.startsWith("[")) {
                    val arr = JSONArray(responseStr)
                    if (arr.length() > 0) {
                        val generatedText = arr.getJSONObject(0).optString("generated_text", "")
                        if (generatedText.isNotBlank()) return@withContext generatedText
                    }
                } else if (responseStr.startsWith("{")) {
                    val obj = JSONObject(responseStr)
                    val text = obj.optString("generated_text", "")
                    if (text.isNotBlank()) return@withContext text
                }

                "Response received from $modelId:\n$responseStr"
            }
        } catch (e: Exception) {
            "Network / Hugging Face Error: ${e.localizedMessage ?: e.message}"
        }
    }

    /**
     * Searches Datasets and Spaces on Hugging Face Hub.
     */
    suspend fun searchHubItems(
        query: String,
        limit: Int = 6
    ): List<HuggingFaceHubItem> = withContext(Dispatchers.IO) {
        val list = mutableListOf<HuggingFaceHubItem>()
        if (query.isBlank()) return@withContext list

        // Search Datasets
        try {
            val dsUrl = "https://huggingface.co/api/datasets?search=${query.trim()}&limit=$limit"
            val request = Request.Builder().url(dsUrl).get().build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val str = response.body?.string() ?: ""
                    val arr = JSONArray(str)
                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        val id = item.optString("id", "")
                        val downloads = item.optInt("downloads", 0)
                        val likes = item.optInt("likes", 0)
                        if (id.isNotBlank()) {
                            list.add(HuggingFaceHubItem(id, "Dataset", likes, downloads, "data"))
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // Search Spaces
        try {
            val spUrl = "https://huggingface.co/api/spaces?search=${query.trim()}&limit=$limit"
            val request = Request.Builder().url(spUrl).get().build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val str = response.body?.string() ?: ""
                    val arr = JSONArray(str)
                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        val id = item.optString("id", "")
                        val likes = item.optInt("likes", 0)
                        val sdk = item.optString("sdk", "gradio")
                        if (id.isNotBlank()) {
                            list.add(HuggingFaceHubItem(id, "Space", likes, 0, sdk))
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        list
    }
}
