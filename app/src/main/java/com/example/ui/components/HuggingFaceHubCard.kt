package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.remote.HuggingFaceApi
import com.example.data.remote.HuggingFaceHubItem
import com.example.data.remote.HuggingFaceModelInfo
import kotlinx.coroutines.launch

/**
 * Interactive Hugging Face Docs & Inference Hub Component
 * Integrates Hugging Face API (models, datasets, spaces, serverless inference)
 * and direct links to Hugging Face Documentation (https://huggingface.co/docs).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HuggingFaceHubCard(
    userApiKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Docs & Explorer, 1: Model Hub, 2: Serverless Inference
    var searchQuery by remember { mutableStateOf("llama") }
    var isSearching by remember { mutableStateOf(false) }
    var modelResults by remember { mutableStateOf<List<HuggingFaceModelInfo>>(emptyList()) }
    var hubItemResults by remember { mutableStateOf<List<HuggingFaceHubItem>>(emptyList()) }

    // Serverless Inference state
    var selectedModelId by remember { mutableStateOf("meta-llama/Llama-3.2-1B-Instruct") }
    var inferencePrompt by remember { mutableStateOf("Write a Kotlin extension function to convert JsonObject to Map<String, Any> in Android.") }
    var inferenceResponse by remember { mutableStateOf("") }
    var isInferring by remember { mutableStateOf(false) }
    var hfAccessToken by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hugging_face_hub_card"),
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
                        contentDescription = "Hugging Face",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hugging Face Platform & Hub",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "HF Docs & AI Hub",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().testTag("hf_tab_row")
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Docs & Overview", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.height(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        if (modelResults.isEmpty()) {
                            isSearching = true
                            scope.launch {
                                modelResults = HuggingFaceApi.searchModels(searchQuery)
                                hubItemResults = HuggingFaceApi.searchHubItems(searchQuery)
                                isSearching = false
                            }
                        }
                    },
                    text = { Text("Model Hub", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.height(18.dp)) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Serverless AI", fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.height(18.dp)) }
                )
            }

            // TAB 0: HF Documentation & Quick Overview
            if (selectedTabIndex == 0) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Official Hugging Face Documentation & Ecosystem Reference",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Hugging Face is the open machine learning platform for models, datasets, and applications. " +
                                "Explore transformers, diffusers, datasets, spaces, and serverless inference APIs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Documentation Quick Links
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Key Documentation References:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        listOf(
                            "Hugging Face Docs Home" to "https://huggingface.co/docs",
                            "Transformers Library" to "https://huggingface.co/docs/transformers/index",
                            "Serverless Inference API" to "https://huggingface.co/docs/api-inference/index",
                            "Hub Python / REST API" to "https://huggingface.co/docs/huggingface_hub/index",
                            "Datasets & Spaces" to "https://huggingface.co/docs/datasets/index"
                        ).forEach { (docTitle, docUrl) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.height(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = docTitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        runCatching {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(docUrl))
                                            context.startActivity(intent)
                                        }.onFailure {
                                            Toast.makeText(context, docUrl, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Open", fontSize = 11.sp)
                                        Spacer(Modifier.width(2.dp))
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://huggingface.co/docs"))
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(context, "Visit: https://huggingface.co/docs", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_open_hf_docs")
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Visit huggingface.co/docs in Browser")
                    }
                }
            }

            // TAB 1: Live Model & Hub Search
            if (selectedTabIndex == 1) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search HF Models / Datasets") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hf_search_input"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    isSearching = true
                                    scope.launch {
                                        modelResults = HuggingFaceApi.searchModels(searchQuery)
                                        hubItemResults = HuggingFaceApi.searchHubItems(searchQuery)
                                        isSearching = false
                                    }
                                }
                            },
                            enabled = !isSearching,
                            modifier = Modifier.testTag("btn_hf_search")
                        ) {
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Search")
                            }
                        }
                    }

                    if (modelResults.isNotEmpty()) {
                        Text(
                            text = "Model Hub Results (${modelResults.size}):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        modelResults.take(6).forEach { model ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = model.id,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Tag: ${model.pipelineTag}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "❤️ ${model.likes}",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            Text(
                                                text = "📥 ${model.downloads}",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            selectedModelId = model.id
                                            selectedTabIndex = 2
                                            Toast.makeText(context, "Selected model: ${model.id}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text("Use Model", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (hubItemResults.isNotEmpty()) {
                        Text(
                            text = "Datasets & Spaces:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        hubItemResults.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "[${item.type}] ${item.id}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "❤️ ${item.likes}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }

            // TAB 2: Serverless Inference API
            if (selectedTabIndex == 2) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select Preset Open Model:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HuggingFaceApi.FEATURED_MODELS.forEach { (modelId, label) ->
                            FilterChip(
                                selected = selectedModelId == modelId,
                                onClick = { selectedModelId = modelId },
                                label = { Text(modelId.substringAfter("/"), fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = selectedModelId,
                        onValueChange = { selectedModelId = it },
                        label = { Text("Target Hugging Face Model ID") },
                        modifier = Modifier.fillMaxWidth().testTag("hf_selected_model_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                    )

                    OutlinedTextField(
                        value = hfAccessToken,
                        onValueChange = { hfAccessToken = it },
                        label = { Text("Hugging Face Access Token (Optional)") },
                        placeholder = { Text("hf_xxxxxxxx (Optional for higher rate limits)") },
                        modifier = Modifier.fillMaxWidth().testTag("hf_token_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = inferencePrompt,
                        onValueChange = { inferencePrompt = it },
                        label = { Text("Inference Prompt") },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("hf_inference_prompt"),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    Button(
                        onClick = {
                            if (inferencePrompt.isNotBlank()) {
                                isInferring = true
                                inferenceResponse = "Querying Hugging Face Serverless Inference for $selectedModelId..."
                                scope.launch {
                                    inferenceResponse = HuggingFaceApi.runInference(
                                        prompt = inferencePrompt,
                                        modelId = selectedModelId,
                                        hfToken = hfAccessToken
                                    )
                                    isInferring = false
                                }
                            }
                        },
                        enabled = !isInferring,
                        modifier = Modifier.fillMaxWidth().testTag("btn_run_hf_inference")
                    ) {
                        if (isInferring) {
                            CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Running Hugging Face Inference...")
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Run Hugging Face Model Inference")
                        }
                    }

                    if (inferenceResponse.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Inference Output:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = inferenceResponse,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
