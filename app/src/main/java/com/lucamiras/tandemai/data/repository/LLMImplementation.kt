package com.lucamiras.tandemai.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.generationConfig
import com.lucamiras.tandemai.BuildConfig
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.Message
import com.lucamiras.tandemai.data.model.Scenario
import com.lucamiras.tandemai.data.model.SkillLevel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow


class LLMAPIClient (language: StateFlow<Language>,
                    skillLevel: StateFlow<SkillLevel>,
                    scenario: StateFlow<Scenario>,
                    modelName: String = "gemini-1.5-flash"){

    private val _language = language
    private val _skillLevel = skillLevel
    private val _scenario = scenario
    private val _modelName = modelName

    suspend fun callLLMAPIEndpoint(message: String,
                                   systemInstruction: SystemInstructions,
                                   chatHistory: StateFlow<List<Content>>) : String {

        val systemInstructionContent = systemInstruction.composeSystemInstruction(_language, _skillLevel, _scenario)
        val responseFormat = systemInstruction.responseType

        val model = GenerativeModel(
                modelName = _modelName,
                apiKey = BuildConfig.GEMINI_API_KEY,
                systemInstruction = systemInstructionContent,
                generationConfig = generationConfig {
                    temperature = 1f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 512
                    responseMimeType = responseFormat
                }
            )
        val chat = model.startChat(history = chatHistory.value)
        val response = chat.sendMessage(message)
        val responseString = response.candidates[0].content.parts[0].asTextOrNull().toString()
        return responseString
    }
}

class LLMImplementation(private val apiClient: LLMAPIClient) : LLMRepository{
    override fun sendMessageToLLM(
        message: String,
        systemInstructions: SystemInstructions,
        chatHistory: StateFlow<List<Content>>
    ): Flow<Message> = flow {
        coroutineScope {
            val response = apiClient.callLLMAPIEndpoint(
                message = message,
                systemInstruction = systemInstructions,
                chatHistory = chatHistory
            )
            val llmMessage = Message(
                messageId = 0,
                senderRole = "model",
                messageContent = response
            )
            emit(llmMessage)
        }
    }
}