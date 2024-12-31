package com.lucamiras.tandemai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Mistake(
    val id: Int,
    val description: String
)