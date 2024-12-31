package com.lucamiras.tandemai.ui.featureMistakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucamiras.tandemai.data.model.Mistake

class MistakesViewModel : ViewModel() {
    private val _mistakes = MutableLiveData<List<Mistake>>(emptyList())
    val mistakes = _mistakes

    fun addMistakeToViewModel(mistake: Mistake) {
        _mistakes.value = _mistakes.value!! + mistake
    }
}