package com.example.data.mapper

import com.example.data.remote.dto.response.messages.MessageDto
import com.example.data.remote.dto.response.presence.PresenceDto
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.Presence

fun MessageDto.toEntity(): ChatMessage = ChatMessage(
    id = id,
    from = from,
    to = to,
    content = content,
    createdAt = createdAt
)

fun PresenceDto.toEntity(userId: String): Presence = Presence(
    userId = this.userId ?: userId,
    online = status == "online",
    lastSeen = lastSeen
)
