package com.aistudio.builder.app.data.repository

import com.aistudio.builder.app.data.db.AiLogEntity
import com.aistudio.builder.app.data.db.AppDao
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
