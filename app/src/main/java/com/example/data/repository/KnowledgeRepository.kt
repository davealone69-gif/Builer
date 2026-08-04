package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.BookmarkEntity
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
