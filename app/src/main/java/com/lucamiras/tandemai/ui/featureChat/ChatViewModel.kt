package com.lucamiras.tandemai.ui.featureChat

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.lucamiras.tandemai.data.model.Message
import com.lucamiras.tandemai.data.repository.ChatSystemInstruction
import com.lucamiras.tandemai.data.repository.LLMRepository
import kotlinx.coroutines.launch

class ChatViewModel(private val llmRepository: LLMRepository) : ViewModel() {
    private val _chatHistory = MutableLiveData<List<Content>>(emptyList())
    val chatHistory = _chatHistory

    fun addNewConversation(message: String) {
        viewModelScope.launch {
            val messageContent = content("user"){text(message)}
            _chatHistory.value = _chatHistory.value!! + messageContent
            // Test
            val llmResponse = content("model"){text("Example response")}
            _chatHistory.value = _chatHistory.value!! + llmResponse
        }

    }
}