package com.aistudio.builder.app.ui.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aistudio.builder.app.data.db.BookmarkEntity

data class KnowledgeTopic(
    val title: String,
    val category: String,
    val summary: String,
    val details: String
)

val KNOWLEDGE_TOPICS = listOf(
    KnowledgeTopic(
        title = "Android Developers Portal",
        category = "Android Docs",
        summary = "Official Google Android Developers documentation, API references, Jetpack guidelines, and Kotlin guides (https://developer.android.com/).",
        details = "Comprehensive guides on Jetpack Compose, Android SDK, Gradle configurations, Material Design 3, App Architecture, and official API documentation. Website: https://developer.android.com/"
    ),
    KnowledgeTopic(
        title = "ZenML MLOps Framework",
        category = "MLOps",
        summary = "Extensible, open-source MLOps framework for building production machine learning pipelines (https://www.zenml.io/).",
        details = "ZenML connects data stacks, model registries, training tools, and cloud orchestration into reproducible ML workflows. Website: https://www.zenml.io/"
    ),
    KnowledgeTopic(
        title = "ByteByteGo System Design",
        category = "System Design",
        summary = "Comprehensive system design diagrams, architectural patterns, and scalability courses (https://bytebytego.com/).",
        details = "ByteByteGo covers distributed systems, high availability, microservices, database sharding, caching strategies, and technical architecture. Website: https://bytebytego.com/"
    ),
    KnowledgeTopic(
        title = "Jetpack Compose State Management",
        category = "Compose",
        summary = "Best practices for state hoisting, MutableStateFlow, and collectAsStateWithLifecycle.",
        details = "Always expose StateFlow from ViewModels. Collect using collectAsStateWithLifecycle() in Composables to avoid resource leaks when app is in background."
    ),
    KnowledgeTopic(
        title = "Room Database Architecture",
        category = "Storage",
        summary = "Local SQLite ORM using KSP annotation processing.",
        details = "Define @Entity, @Dao interfaces with suspend or Flow return types, and @Database class with getInstance singleton pattern."
    ),
    KnowledgeTopic(
        title = "Gemini API Integration Best Practices",
        category = "AI / ML",
        summary = "REST API calls with secrets injected via BuildConfig.",
        details = "Access API keys safely using BuildConfig.GEMINI_API_KEY injected via .env / Secrets panel. Set OkHttpClient timeouts to 60s for long model responses."
    ),
    KnowledgeTopic(
        title = "Clean Architecture MVVM Pattern",
        category = "Architecture",
        summary = "Layer separation between UI, Domain, and Data layer.",
        details = "UI (Composables + ViewModel) -> Domain (UseCases) -> Data (Repositories + Room/Retrofit DataSources). Keeps business logic isolated and testable."
    )
)

@Composable
fun KnowledgeModule(
    bookmarks: List<BookmarkEntity>,
    onAddBookmark: (title: String, category: String, content: String) -> Unit,
    onDeleteBookmark: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Android") }
    var newContent by remember { mutableStateOf("") }

    val filteredTopics = KNOWLEDGE_TOPICS.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true) ||
        it.summary.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("knowledge_module"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Knowledge — Architecture & Technical Reference",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("knowledge_search_input"),
                label = { Text("Search Cheat Sheets & Guides") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
        }

        // Built-in Knowledge Reference List
        item {
            Text(
                text = "Architecture Cheat Sheets (${filteredTopics.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(filteredTopics) { topic ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topic.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.extraSmall
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = topic.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = topic.details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            onAddBookmark(topic.title, topic.category, topic.details)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Save Bookmark")
                    }
                }
            }
        }

        // Custom Bookmark Entry Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Add Custom Note / Bookmark to Room DB",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Content / Details") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (newTitle.isNotBlank()) {
                                onAddBookmark(newTitle, newCategory, newContent)
                                newTitle = ""
                                newContent = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Add Custom Bookmark")
                    }
                }
            }
        }

        // Room Database Bookmarks List
        item {
            Text(
                text = "Saved Bookmarks in Room Database (${bookmarks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (bookmarks.isEmpty()) {
            item {
                Text(
                    text = "No saved bookmarks yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(bookmarks, key = { it.id }) { bm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bm.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = bm.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteBookmark(bm.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
