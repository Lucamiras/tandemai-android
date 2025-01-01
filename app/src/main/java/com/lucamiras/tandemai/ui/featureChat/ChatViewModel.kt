package com.lucamiras.tandemai.ui.featureChat

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.lucamiras.tandemai.data.model.Message
import com.lucamiras.tandemai.data.model.Mistake
import com.lucamiras.tandemai.data.repository.ChatSystemInstruction
import com.lucamiras.tandemai.data.repository.LLMAPIClient
import com.lucamiras.tandemai.data.repository.LLMImplementation
import com.lucamiras.tandemai.data.repository.LLMRepository
import com.lucamiras.tandemai.data.repository.MistakeSystemInstruction
import com.lucamiras.tandemai.data.repository.SystemInstructions
import com.lucamiras.tandemai.ui.featureMistakes.MistakesViewModel
import com.lucamiras.tandemai.ui.featureSetup.SetupViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<Content>>(emptyList())
    val chatHistory: StateFlow<List<Content>> = _chatHistory.asStateFlow()

    fun addNewConversation(message: String,
                           systemInstructions: SystemInstructions,
                           setupViewModel: SetupViewModel) {

        // Add new message to chat history
        val messageContent = content("user"){text(message)}
        _chatHistory.value += messageContent

        // Initialize LLM with current setup
        val llm = initializeLLMClient(setupViewModel)

        // Launch coroutine to handle LLM call
        viewModelScope.launch {
           llm.sendMessageToLLM(
                message=message,
                systemInstructions = systemInstructions,
                chatHistory = chatHistory).collect { llmResponse ->
                _chatHistory.value += content("model") { text(llmResponse.messageContent) }
            }
        }
    }

    fun evaluateMistakes(systemInstructions: SystemInstructions, setupViewModel: SetupViewModel, mistakesViewModel: MistakesViewModel) {

        // Get last two messages ( one from model, one from user )
        val latestMessages = _chatHistory.value.takeLast(2)
        if (latestMessages.size != 2) {
            return
        }
        val modelMessage = latestMessages[0].parts[0].asTextOrNull().toString()
        val userMessage = latestMessages[1].parts[0].asTextOrNull().toString()
        val formattedMessages = """
            Tandem Partner wrote: $modelMessage
            User responded (CORRECT THIS IF YOU FIND ANY MISTAKES): $userMessage 
        """.trimIndent()


        // Initialize LLM with current setup
        val llm = initializeLLMClient(setupViewModel)

        viewModelScope.launch {
            llm.sendMessageToLLM(
                message=formattedMessages,
                systemInstructions=MistakeSystemInstruction,
                chatHistory = chatHistory).collect { llmResponse ->
                    val mistakeId = mistakesViewModel.generateMistakeId()
                    if (llmResponse.messageContent != "No mistakes\n") {
                        mistakesViewModel.addMistakeToViewModel(
                            Mistake(
                                id = mistakeId,
                                description = llmResponse.messageContent
                            ))
                    } else {
                        return@collect
                    }
            }
        }
    }

    private fun initializeLLMClient(setupViewModel: SetupViewModel) : LLMImplementation {
        val language = setupViewModel.selectedLanguage
        val skillLevel = setupViewModel.selectedSkillLevel
        return (LLMImplementation(LLMAPIClient(language, skillLevel)))
    }
}