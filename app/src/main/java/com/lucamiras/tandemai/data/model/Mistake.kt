package com.lucamiras.tandemai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Mistake(
    var id: Int,
    val language: String,
    val originalSentence: String,
    val errorType: String,
    val feedback: String,
)
