package com.lucamiras.tandemai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.lucamiras.tandemai.src.Mistake


class SharedViewModel() : ViewModel() {
    var mistakes = MutableLiveData<MutableList<Mistake>>()

    fun addToMistakes(mistake: Mistake) {
        mistakes.value?.add(mistake)
    }
}