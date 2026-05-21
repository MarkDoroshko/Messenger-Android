package com.example.data.repository

import com.example.data.mapper.toEntity
import com.example.data.remote.api.messages.MessagesApi
import com.example.data.remote.ws.MessengerSocket
import com.example.domain.entity.ChatMessage
import com.example.domain.repository.MessageRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val api: MessagesApi,
    private val socket: MessengerSocket
) : MessageRepository {

    override val incoming: SharedFlow<ChatMessage> get() = socket.incoming

    override suspend fun getHistory(peerId: String, limit: Int): Result<List<ChatMessage>> =
        runCatching { api.getHistory(peerId, limit).items.map { it.toEntity() } }

    override suspend fun connect(): Result<Unit> = runCatching { socket.connect() }

    override suspend fun disconnect() { socket.disconnect() }

    override suspend fun send(toUserId: String, content: String, clientMessageId: String): Result<Unit> =
        runCatching { socket.send(toUserId, content, clientMessageId) }
}
