package com.example.data.remote.dto.response.messages

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val from: String,
    val to: String,
    val content: String,
    val createdAt: String
)

@Serializable
data class HistoryResponse(
    val items: List<MessageDto>
)
