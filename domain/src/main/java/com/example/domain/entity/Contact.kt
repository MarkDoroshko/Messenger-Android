package com.example.domain.entity

data class Contact(
    val peerId: String,
    val displayName: String,
    val createdAt: String,
    val lastMessage: LastMessagePreview?
)

data class LastMessagePreview(
    val content: String,
    val createdAt: String,
    val fromMe: Boolean
)
