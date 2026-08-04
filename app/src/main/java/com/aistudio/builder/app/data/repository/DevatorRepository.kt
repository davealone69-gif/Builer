package com.aistudio.builder.app.data.repository

import com.aistudio.builder.app.data.db.AppDao
import com.aistudio.builder.app.data.db.CodeSnippetEntity
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
