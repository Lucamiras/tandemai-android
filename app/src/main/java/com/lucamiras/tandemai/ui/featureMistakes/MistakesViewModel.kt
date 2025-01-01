package com.lucamiras.tandemai.ui.featureMistakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.type.Content
import com.lucamiras.tandemai.data.model.Mistake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MistakesViewModel : ViewModel() {
    private val _mistakes = MutableStateFlow<List<Mistake>>(emptyList())
    val mistakes: StateFlow<List<Mistake>> = _mistakes.asStateFlow()

    fun addMistakeToViewModel(mistake: Mistake) {
        _mistakes.value += mistake
    }

    fun generateMistakeId() : Int {
        return mistakes.value.size + 1
    }

    fun clearMistakes() {
        _mistakes.value = emptyList()
    }
}