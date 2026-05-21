package com.example.data.remote.dto.ws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WsOutgoing {
    @Serializable
    @SerialName("message")
    data class Message(
        val to: String,
        val content: String,
        val clientMessageId: String
    ) : WsOutgoing

    @Serializable
    @SerialName("ping")
    data object Ping : WsOutgoing
}

@Serializable
data class WsIncomingMessage(
    val id: String,
    val from: String,
    val to: String,
    val content: String,
    val createdAt: String
)
