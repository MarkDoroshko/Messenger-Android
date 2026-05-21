package com.example.domain.repository

import com.example.domain.entity.Presence
import kotlinx.coroutines.flow.SharedFlow

interface PresenceRepository {
    suspend fun get(userId: String): Result<Presence>

    /** Push-апдейты статусов от сервера (по подпискам). */
    val updates: SharedFlow<Presence>

    /** Подписаться на push-апдейты этих пользователей. Идемпотентно. */
    suspend fun subscribe(userIds: Collection<String>)

    /** Отписаться. */
    suspend fun unsubscribe(userIds: Collection<String>)
}
