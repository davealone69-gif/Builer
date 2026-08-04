package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "code_snippets")
data class CodeSnippetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val language: String,
    val code: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_logs")
data class AiLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val response: String,
    val model: String,
    val timestamp: Long = System.currentTimeMillis()
)
