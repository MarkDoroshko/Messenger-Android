package com.example.data.remote.api.messages

import com.example.data.remote.dto.response.messages.HistoryResponse

interface MessagesApi {
    suspend fun getHistory(peerId: String, limit: Int): HistoryResponse
}
