package com.lucamiras.tandemai.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Message (
    val messageId: Int,
    val senderRole: String,
    val messageContent: String
)