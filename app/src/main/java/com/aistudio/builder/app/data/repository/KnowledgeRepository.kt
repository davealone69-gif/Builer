package com.aistudio.builder.app.data.repository

import com.aistudio.builder.app.data.db.AppDao
import com.aistudio.builder.app.data.db.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class KnowledgeRepository(private val dao: AppDao) {
    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    suspend fun addBookmark(bookmark: BookmarkEntity) {
        dao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmark(id: Long) {
        dao.deleteBookmark(id)
    }
}
