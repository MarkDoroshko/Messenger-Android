package com.example.data.remote.api.presence

import com.example.data.remote.dto.response.presence.PresenceDto
import com.example.data.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class PresenceApiImpl @Inject constructor(
    private val client: HttpClient
) : PresenceApi {
    override suspend fun get(userId: String): PresenceDto {
        return client.get("${Constants.BASE_URL}/presence/$userId").body()
    }
}
