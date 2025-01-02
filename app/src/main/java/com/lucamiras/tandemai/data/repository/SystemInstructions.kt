package com.lucamiras.tandemai.data.repository

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.lucamiras.tandemai.data.model.Language
import com.lucamiras.tandemai.data.model.Scenario
import com.lucamiras.tandemai.data.model.SkillLevel
import kotlinx.coroutines.flow.StateFlow


interface SystemInstructions {
    val template: String
    val responseType: String
    fun composeSystemInstruction(language: StateFlow<Language>,
                                 skillLevel: StateFlow<SkillLevel>,
                                 scenario: StateFlow<Scenario>) : Content
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
        {specificScenarioInstructions}
    """

    override fun composeSystemInstruction(language: StateFlow<Language>,
                                          skillLevel: StateFlow<SkillLevel>,
                                          scenario: StateFlow<Scenario>): Content {
        val languageName = language.value.name
        val skillLevelName = skillLevel.value.name
        val specificSkillLevelInstructions = when (skillLevel.value) {
            SkillLevel.Beginner -> "focus on basic grammar and vocabulary."
            SkillLevel.Intermediate -> "introduce more complex sentences, common expressions and different tenses."
            SkillLevel.Advanced -> "engage in nuanced discussions with complex sentence structures."
            SkillLevel.Native -> "communicate as if the user is a native speaker in the language."
        }
        val specificScenarioInstructions = when (scenario.value) {
            Scenario.JOB -> "In this scenario, you are role-playing. The scenario is JOB INTERVIEW. You will play the role of an interviewer. Please tailor all your responses accordingly."
            Scenario.RESTAURANT -> "In this scenario, you are role-playing. The scenario is RESTAURANT. You will play the role of the waiter. Please tailor all your responses accordingly."
            else -> ""
        }

        return (
                content(role="model") {
                    text(template
                        .replace("{language}", languageName)
                        .replace("{skillLevel}", skillLevelName)
                        .replace("{specificSkillLevelInstructions}", specificSkillLevelInstructions)
                        .replace("{specificScenarioInstructions", specificScenarioInstructions))
                })
    }
}

object MistakeSystemInstruction : SystemInstructions {
    override val responseType = "application/json"
    override val template = """
        You are a helpful language learning assistant.
        The user is chatting with another language learning assistant.
        The user is learning {language} and has a {skilllevel} skill level.
        Your task is to look at the response from the user and point out mistakes.
        However, you are to ignore mistakes like a missing comma, an additional white space, or informal language. 
        Remember, you are correcting conversational {language}, NOT correcting an essay in university.
        Only point out obvious errors in grammar or spelling. Allow for slang.
        
        You must respond in valid JSON. Never add any additional information but the answer itself.
        You must use the following JSON keys: id, language, originalSentence, errorType, feedback.
        
        Example:
            {
                "id": 0,
                "language":"en-us",
                "originalSentence":"I are very hungry",
                "errorType":"verb configuration",
                "feedback":"Instead of 'I are very hungry', you should write 'I am very hungry'.
            }
            
        If the user made no mistakes, please return an empty object like this, where errorType and feedback are both "none".
        
        Example:
            {
                "id": 0,
                "language":"en-us",
                "originalSentence":"I am very hungry",
                "errorType":"none",
                "feedback":"none"
            }
    """.trimIndent()

    override fun composeSystemInstruction(language: StateFlow<Language>,
                                          skillLevel: StateFlow<SkillLevel>,
                                          scenario: StateFlow<Scenario>): Content {
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

object OpeningSystemInstruction : SystemInstructions {
    override val responseType = "text/plain"
    override val template = """
        You are a helpful language learning assistant, or Tandem Partner.
        The user you are talking to is learning {language} and has a {skilllevel} skill level.
        {specificScenarioInstructions} 
        You can choose to write it, but it MUST always be in {language}. 
        No other language is acceptable.
        Respond in plain text. Never add any additional information but the answer itself.
        
        Good examples: {specificScenarioExamples}
        
        Bad example: "Sure, here is my opening response: Hi, what's up?"
    """.trimIndent()

    override fun composeSystemInstruction(language: StateFlow<Language>,
                                          skillLevel: StateFlow<SkillLevel>,
                                          scenario: StateFlow<Scenario>): Content {
        val languageName = language.value.name
        val specificScenarioInstructions: Map<String, String> = when (scenario.value) {
            Scenario.JOB -> mapOf(
                "instruction" to "You are role-playing as a job interviewer. Write an opening line to the conversation.",
                "example" to "'Welcome to our company', 'Hi, thanks for coming in!'")
            Scenario.RESTAURANT -> mapOf(
                "instruction" to "You are role-playing as a waiter at a restaurant. Write an opening line to the conversation.",
                "example" to "'Good evening. Welcome to our restaurant.', 'Good evening. May I bring tell you about our specials tonight?'")
            else -> mapOf(
                "instruction" to "Please write the opening greeting.",
                "example" to "'Hi, how are you?', 'Good day!', 'What\'s up'?")
        }

        return (
                content(role="model") {
                    text(
                        ChatSystemInstruction.template
                        .replace("{language}", languageName)
                        .replace("{specificScenarioInstructions", specificScenarioInstructions.getValue("instruction"))
                        .replace("{specificScenarioExamples", specificScenarioInstructions.getValue("example"))
                    )
                })
    }
}