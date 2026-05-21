package com.example.data.mapper

import com.example.data.remote.dto.response.contacts.ContactDto
import com.example.data.remote.dto.response.contacts.LastMessageDto
import com.example.domain.entity.Contact
import com.example.domain.entity.LastMessagePreview

fun ContactDto.toEntity(): Contact = Contact(
    peerId = peerId,
    displayName = displayName,
    createdAt = createdAt,
    lastMessage = lastMessage?.toEntity()
)

fun LastMessageDto.toEntity(): LastMessagePreview = LastMessagePreview(
    content = content,
    createdAt = createdAt,
    fromMe = fromMe
)
