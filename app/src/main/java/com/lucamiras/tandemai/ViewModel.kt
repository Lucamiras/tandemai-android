package com.lucamiras.tandemai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.lucamiras.tandemai.src.Mistake


class SharedViewModel() : ViewModel() {
    var mistakes = MutableLiveData<MutableList<Mistake>>(mutableListOf())

    fun addToMistakes(mistake: Mistake) {
        val currentList = mistakes.value ?: mutableListOf()
        currentList.add(mistake)
        mistakes.postValue(currentList)
    }
}