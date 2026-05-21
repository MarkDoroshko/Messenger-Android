package com.example.data.mapper

import com.example.data.remote.dto.response.messages.MessageDto
import com.example.domain.entity.ChatMessage

fun MessageDto.toEntity(): ChatMessage = ChatMessage(
    id = id,
    from = from,
    to = to,
    content = content,
    createdAt = createdAt
)
