package com.lucamiras.tandemai.data.repository

import com.google.ai.client.generativeai.type.Content
import com.lucamiras.tandemai.data.model.Message
import com.lucamiras.tandemai.data.model.SkillLevel
import kotlinx.coroutines.flow.Flow

interface LLMRepository {
    fun sendMessageToLLM(
        message: String,
        systemInstructions: SystemInstructions,
        chatHistory: MutableList<Content>
    ): Flow<Message>
}