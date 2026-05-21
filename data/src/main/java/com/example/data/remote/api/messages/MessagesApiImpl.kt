package com.example.data.remote.api.messages

import com.example.data.remote.dto.response.messages.HistoryResponse
import com.example.data.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class MessagesApiImpl @Inject constructor(
    private val client: HttpClient
) : MessagesApi {
    override suspend fun getHistory(peerId: String, limit: Int): HistoryResponse {
        return client.get("${Constants.BASE_URL}/messages/with/$peerId") {
            parameter("limit", limit)
        }.body()
    }
}
