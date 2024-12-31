package com.lucamiras.tandemai.ui.featureSetup

import androidx.lifecycle.ViewModel
import com.lucamiras.tandemai.data.model.SkillLevel
import com.lucamiras.tandemai.data.model.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetupViewModel : ViewModel() {
    private val _selectedLanguage = MutableStateFlow<Language>(Language.English)
    val selectedLanguage = _selectedLanguage.asStateFlow()
    private val _selectedSkillLevel = MutableStateFlow(SkillLevel.Beginner)
    val selectedSkillLevel = _selectedSkillLevel.asStateFlow()

    fun setLanguage(language: Language) {
        _selectedLanguage.value = language
    }
    fun setSkillLevel(skillLevel: SkillLevel) {
        _selectedSkillLevel.value = skillLevel
    }
}