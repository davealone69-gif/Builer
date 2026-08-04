package com.example.data.repository

import com.example.data.db.AiLogEntity
import com.example.data.db.AppDao
import kotlinx.coroutines.flow.Flow

class AiLogRepository(private val dao: AppDao) {
    val allLogs: Flow<List<AiLogEntity>> = dao.getAllAiLogs()

    suspend fun logInteraction(prompt: String, response: String, model: String) {
        dao.insertAiLog(AiLogEntity(prompt = prompt, response = response, model = model))
    }

    suspend fun clearLogs() {
        dao.clearAiLogs()
    }
}
