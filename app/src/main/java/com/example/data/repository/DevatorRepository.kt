package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.CodeSnippetEntity
import kotlinx.coroutines.flow.Flow

class DevatorRepository(private val dao: AppDao) {
    val allSnippets: Flow<List<CodeSnippetEntity>> = dao.getAllSnippets()

    suspend fun saveSnippet(snippet: CodeSnippetEntity) {
        dao.insertSnippet(snippet)
    }

    suspend fun deleteSnippet(id: Long) {
        dao.deleteSnippet(id)
    }
}
