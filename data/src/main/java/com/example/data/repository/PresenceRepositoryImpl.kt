package com.example.data.repository

import com.example.data.mapper.toEntity
import com.example.data.remote.api.presence.PresenceApi
import com.example.data.remote.ws.MessengerSocket
import com.example.domain.entity.Presence
import com.example.domain.repository.PresenceRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenceRepositoryImpl @Inject constructor(
    private val api: PresenceApi,
    private val socket: MessengerSocket
) : PresenceRepository {
    override suspend fun get(userId: String): Result<Presence> =
        runCatching { api.get(userId).toEntity(userId) }

    override val updates: SharedFlow<Presence> get() = socket.presence

    override suspend fun subscribe(userIds: Collection<String>) {
        socket.subscribePresence(userIds)
    }

    override suspend fun unsubscribe(userIds: Collection<String>) {
        socket.unsubscribePresence(userIds)
    }
}
