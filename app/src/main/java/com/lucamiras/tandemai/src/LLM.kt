package com.lucamiras.tandemai.src

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.lucamiras.tandemai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import android.util.Log


@Serializable
data class Mistake(
    val id: Int,
    val description: String
)

@Serializable
data class Response(
    val chatResponse: String,
    val mistakes: List<String>
)

class LLMClient {

    private lateinit var _systemMessage: Content
    private lateinit var _model: GenerativeModel
    private val _history: MutableList<Content> = mutableListOf<Content>()
    private val _mistakes: SnapshotStateList<Mistake> = mutableStateListOf<Mistake>()
    private val _mistakesCount = MutableStateFlow(0)
    private val _mistakesColor = MutableStateFlow(Color.Gray)
    val mistakesCount: StateFlow<Int> = _mistakesCount
    val mistakesColor: StateFlow<Color> = _mistakesColor
    val mistakesList: SnapshotStateList<Mistake> = _mistakes
    private val _nextMistakeId = if (mistakesList.isNotEmpty()) {
        mistakesList.last().id + 1
    } else {
        0
    }

    fun initializePartner(language: String?, level: String?) {
        try {
            _systemMessage = content(role="model") {
                text(
                    """ You are a helpful tandem partner, helping this user to learn $language. 
                Start the conversation by saying 'Hi' and asking a question in $language that the 
                user can answer. Only ever respond in $language, never in any other language.
                The user's skill level is $level. Please tailor ALL your responses to match their level.
                Respond in valid JSON.  Use this schema:
                Response = {'chatResponse': str, 'mistakes': list[str]}
                Return Response
                'chatResponse' is your response to the user input.
                'mistakes' is any mistakes the user made. Corrections MUST BE in English.
                Ensure you ONLY respond with JSON, nothing else. Your answer MUST start with {""".trimMargin())}

            val model by lazy {
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = _systemMessage,
                    generationConfig = generationConfig {
                        temperature = 1f
                        topK = 40
                        topP = 0.95f
                        maxOutputTokens = 8192
                        responseMimeType = "application/json" },
                )
            }

            _model = model
            Log.w("MYTAG", "Model was initialized successfully!")
        } catch (e: Exception) {
            Log.w("MYTAG", "Model could not be initialized")
        }
    }

    private fun parseJsonFromText(jsonString: String): Response {
        val jsonOutput = Json.decodeFromString<Response>(jsonString)
        return (jsonOutput)
    }

    private suspend fun generateResponses(message: String): String {

        val chat = _model.startChat(history = _history)
        val jsonString = chat.sendMessage(message).candidates[0].content.parts[0].asTextOrNull().toString()
        val parsedJson: Response = try {
            parseJsonFromText(jsonString)
        } catch (e: Exception) {
            Response(
                chatResponse = "ERROR",
                mistakes = listOf("MISTAKE")
            )
        }
        val response = parsedJson.chatResponse
        val mistake = parsedJson.mistakes

        if (mistake.isNotEmpty()) {
            addMistakesToList(mistake)
        }

        _history.add(
            content(role="model") {text(response)}
        )

        return (response)
    }

    private fun addMistakesToList(mistake: List<String>) {
        for (m in mistake) {
            val mistakeObject = Mistake(
                id = _nextMistakeId,
                description = m
            )
            _mistakes.add(mistakeObject)
            _mistakesCount.value += 1
            _mistakesColor.value = Color.Red

        }

    }
    fun callLLM(message: String): String {
        return runBlocking {
            generateResponses(message)
        }
    }
}

