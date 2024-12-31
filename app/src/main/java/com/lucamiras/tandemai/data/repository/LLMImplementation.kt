package com.lucamiras.tandemai.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.generationConfig
import com.lucamiras.tandemai.BuildConfig
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.Message
import com.lucamiras.tandemai.data.model.SkillLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class LLMAPIClient (language: Language,
                    skillLevel: SkillLevel,
                    modelName: String = "gemini-1.5-flash"){

    private val _language = language
    private val _skillLevel = skillLevel
    private val _modelName = modelName

    suspend fun callLLMEndpoint(message: String,
                                systemInstruction: SystemInstructions,
                                chatHistory: MutableList<Content>) : String {

        val systemInstructionContent = systemInstruction.getSystemInstructions(_language, _skillLevel)
        val responseFormat = systemInstruction.responseType

        val model by lazy {
            GenerativeModel(
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
        }
        val chat = model.startChat(history = chatHistory)
        val response = chat.sendMessage(message)
        val responseString = response.candidates[0].content.parts[0].asTextOrNull().toString()
        return responseString
    }
}

class LLMImplementation(private val apiClient: LLMAPIClient) : LLMRepository{
    override fun sendMessageToLLM(message: String, systemInstructions: SystemInstructions, chatHistory: MutableList<Content>): Flow<Message> = flow {
        val response = apiClient.callLLMEndpoint(
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