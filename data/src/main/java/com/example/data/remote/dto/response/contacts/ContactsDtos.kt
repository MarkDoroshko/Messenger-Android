package com.example.data.remote.dto.response.contacts

import com.example.data.remote.dto.response.presence.PresenceDto
import kotlinx.serialization.Serializable

@Serializable
data class LastMessageDto(
    val content: String,
    val createdAt: String,
    val fromMe: Boolean
)

@Serializable
data class ContactDto(
    val peerId: String,
    val displayName: String,
    val createdAt: String,
    val lastMessage: LastMessageDto? = null,
    val presence: PresenceDto
)

@Serializable
data class ContactsResponse(val items: List<ContactDto>)
