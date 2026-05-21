package com.example.data.remote.dto.response.presence

import kotlinx.serialization.Serializable

@Serializable
data class PresenceDto(
    val userId: String? = null,
    val status: String,
    val lastSeen: String? = null,
    val instanceId: String? = null
)
