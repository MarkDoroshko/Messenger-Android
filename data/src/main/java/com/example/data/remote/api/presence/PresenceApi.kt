package com.example.data.remote.api.presence

import com.example.data.remote.dto.response.presence.PresenceDto

interface PresenceApi {
    suspend fun get(userId: String): PresenceDto
}
