package com.example.domain.entity

data class ChatMessage(
    val id: String,
    val from: String,
    val to: String,
    val content: String,
    val createdAt: String
)
