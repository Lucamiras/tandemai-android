package com.lucamiras.tandemai.data.repository

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.SkillLevel
import kotlinx.coroutines.flow.StateFlow


interface SystemInstructions {
    val template: String
    val responseType: String
    fun getSystemInstructions(language: StateFlow<Language>, skillLevel: StateFlow<SkillLevel>) : Content
}

object ChatSystemInstruction : SystemInstructions{
    override val responseType = "text/plain"
    override val template = """
        You are a helpful language learning assistant, or Tandem Partner.
        The user you are talking to is learning {language} and has a {skilllevel} skill level.
        Please have a natural conversation with the user in {language}. 
        You must never answer in any other language BUT {language}.
        Respond in plain text. Never add any additional information but the answer itself.
        
        Example:
            User: "Hi, how are you"
            Correct response: "Hi, I am fine. How are you?"
            Incorrect response: "Sure, here is my response in English: Hi, I am fine. How are you?"
            
        Regarding the skill level, {specificSkillLevelInstructions} 
    """

    override fun getSystemInstructions(language: StateFlow<Language>, skillLevel: StateFlow<SkillLevel>): Content {
        val languageName = language.value.name
        val skillLevelName = skillLevel.value.name
        val specificSkillLevelInstructions = when (skillLevel.value) {
            SkillLevel.Beginner -> "focus on basic grammar and vocabulary."
            SkillLevel.Intermediate -> "introduce more complex sentences, common expressions and different tenses."
            SkillLevel.Advanced -> "engage in nuanced discussions with complex sentence structures."
            SkillLevel.Native -> "communicate as if the user is a native speaker in the language."
        }

        return (
                content(role="model") {
                    text(template
                        .replace("{language}", languageName)
                        .replace("{skillLevel}", skillLevelName)
                        .replace("{specificSkillLevelInstructions}", specificSkillLevelInstructions))
                })
    }
}

object MistakeSystemInstruction : SystemInstructions {
    override val responseType = "text/plain"
    override val template = """
        You are a helpful language learning assistant.
        The user is chatting with another language learning assistant.
        The user is learning {language} and has a {skilllevel} skill level.
        Your task is to look at the response from the user and point out any mistakes.
        Ignore mistakes like a missing comma or an additional white space. Only point out clear errors in grammar or spelling.
        Example:
            User: "I is very hungry."
            Response: "You wrote 'I is very hungry'. The correct response would have been 'I AM very hungry'."
        IF THE USER MADE NO MISTAKES, please return 'No mistakes'.
    """.trimIndent()

    override fun getSystemInstructions(language: StateFlow<Language>, skillLevel: StateFlow<SkillLevel>): Content {
        val languageName = language.value.name
        val skillLevelName = skillLevel.value.name
        return (
                content(role="model") {
                    text(template
                        .replace("{language}", languageName)
                        .replace("{skillLevel}", skillLevelName)
                    )
                })
    }
}