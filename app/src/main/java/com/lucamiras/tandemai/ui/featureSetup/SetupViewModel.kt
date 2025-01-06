package com.lucamiras.tandemai.ui.featureSetup

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.lucamiras.tandemai.data.model.SkillLevel
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.Scenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StaticFieldLeak")
class SetupViewModel: ViewModel() {

    private val _selectedLanguage = MutableStateFlow<Language>(Language.English)
    val selectedLanguage = _selectedLanguage.asStateFlow()
    private val _selectedSkillLevel = MutableStateFlow(SkillLevel.Beginner)
    val selectedSkillLevel = _selectedSkillLevel.asStateFlow()
    private val _selectedScenario = MutableStateFlow(Scenario.NONE)
    val selectedScenario = _selectedScenario.asStateFlow()

    val appName = "Tandem AI"

    fun saveUserSelection(language: Language, skillLevel: SkillLevel, scenario: Scenario) {
        setLanguage(language)
        setSkillLevel(skillLevel)
        setScenario(scenario)
    }

    private fun setLanguage(language: Language) {
        _selectedLanguage.value = language
    }
    private fun setSkillLevel(skillLevel: SkillLevel) {
        _selectedSkillLevel.value = skillLevel
    }
    private fun setScenario(scenario: Scenario) {
        _selectedScenario.value = scenario
    }
}