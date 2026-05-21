package com.example.domain.repository

import com.example.domain.entity.ChatMessage
import kotlinx.coroutines.flow.SharedFlow

interface MessageRepository {
    suspend fun getHistory(peerId: String, limit: Int = 50): Result<List<ChatMessage>>

    /** Открывает WS, если ещё не открыт. Идемпотентно. */
    suspend fun connect(): Result<Unit>

    /** Закрывает WS. */
    suspend fun disconnect()

    /** Поток входящих сообщений с момента подключения. */
    val incoming: SharedFlow<ChatMessage>

    /** Отправить сообщение пиру. clientMessageId — для дедупликации. */
    suspend fun send(toUserId: String, content: String, clientMessageId: String): Result<Unit>
}
