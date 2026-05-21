package com.example.domain.entity

data class Presence(
    val userId: String,
    val online: Boolean,
    val lastSeen: String?
)
