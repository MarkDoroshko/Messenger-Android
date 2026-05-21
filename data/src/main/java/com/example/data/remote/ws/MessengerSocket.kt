package com.example.data.remote.ws

import android.util.Log
import com.example.data.remote.manager.AuthManager
import com.example.data.util.Constants
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.Presence
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessengerSocket @Inject constructor(
    private val client: HttpClient,
    private val authManager: AuthManager,
    private val json: Json
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    private var session: DefaultClientWebSocketSession? = null
    private var listenerJob: Job? = null
    private var reconnectJob: Job? = null

    @Volatile
    private var connectIntent = false

    /** Все userId, на presence которых сейчас должна быть подписка. */
    private val subscribedPresence = mutableSetOf<String>()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _incoming = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incoming: SharedFlow<ChatMessage> = _incoming.asSharedFlow()

    private val _presence = MutableSharedFlow<Presence>(extraBufferCapacity = 64)
    val presence: SharedFlow<Presence> = _presence.asSharedFlow()

    suspend fun connect() {
        mutex.withLock {
            connectIntent = true
            if (session?.isActive == true) return@withLock
            openSessionLocked()
        }
    }

    suspend fun disconnect() {
        mutex.withLock {
            connectIntent = false
            reconnectJob?.cancel()
            reconnectJob = null
            try { session?.close() } catch (_: Throwable) {}
            session = null
            listenerJob?.cancel()
            listenerJob = null
            _connected.value = false
            subscribedPresence.clear()
        }
    }

    suspend fun send(toUserId: String, content: String, clientMessageId: String) {
        if (session?.isActive != true) {
            try { connect() } catch (t: Throwable) {
                Log.w("App", "WS reconnect on send failed", t)
            }
        }
        val s = session ?: throw IllegalStateException("WS не подключён")
        val payload = buildJsonObject {
            put("type", "message")
            put("to", toUserId)
            put("content", content)
            put("clientMessageId", clientMessageId)
        }
        s.send(Frame.Text(json.encodeToString(JsonObject.serializer(), payload)))
    }

    /**
     * Подписаться на presence-апдейты этих юзеров. Идемпотентно.
     * После reconnect'а подписки восстанавливаются автоматически.
     */
    suspend fun subscribePresence(userIds: Collection<String>) {
        if (userIds.isEmpty()) return
        mutex.withLock {
            val added = userIds.filter { subscribedPresence.add(it) }
            if (added.isEmpty()) return@withLock
            val s = session ?: return@withLock
            sendSubscribeFrame(s, "subscribe_presence", added)
        }
    }

    suspend fun unsubscribePresence(userIds: Collection<String>) {
        if (userIds.isEmpty()) return
        mutex.withLock {
            val removed = userIds.filter { subscribedPresence.remove(it) }
            if (removed.isEmpty()) return@withLock
            val s = session ?: return@withLock
            sendSubscribeFrame(s, "unsubscribe_presence", removed)
        }
    }

    private suspend fun sendSubscribeFrame(
        s: DefaultClientWebSocketSession,
        type: String,
        ids: Collection<String>
    ) {
        val payload = buildJsonObject {
            put("type", type)
            put("userIds", buildJsonArray { ids.forEach { add(JsonPrimitive(it)) } })
        }
        try {
            s.send(Frame.Text(json.encodeToString(JsonObject.serializer(), payload)))
        } catch (t: Throwable) {
            Log.w("App", "WS subscribe send failed: ${t.message}")
        }
    }

    /** ВНИМАНИЕ: вызывать только внутри mutex. */
    private suspend fun openSessionLocked() {
        val token = try {
            authManager.getTokens()?.accessToken
        } catch (t: Throwable) { null }
        if (token.isNullOrBlank()) {
            Log.w("App", "WS open skipped — no access token")
            return
        }

        try {
            val newSession = client.webSocketSession {
                url(Constants.WS_URL)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            session = newSession
            _connected.value = true
            Log.i("App", "WS connected")

            // Восстанавливаем подписки на presence после reconnect'а
            if (subscribedPresence.isNotEmpty()) {
                sendSubscribeFrame(newSession, "subscribe_presence", subscribedPresence.toList())
            }

            listenerJob = scope.launch {
                try {
                    for (frame in newSession.incoming) {
                        if (frame is Frame.Text) handle(frame.readText())
                    }
                } catch (t: Throwable) {
                    Log.w("App", "WS read loop error: ${t.message}")
                } finally {
                    val wasIntentional = !connectIntent
                    session = null
                    listenerJob = null
                    _connected.value = false
                    Log.i("App", "WS closed (intentional=$wasIntentional)")
                    if (!wasIntentional) scheduleReconnect()
                }
            }
        } catch (t: Throwable) {
            Log.e("App", "WS open failed: ${t.message}")
            session = null
            _connected.value = false
            if (connectIntent) scheduleReconnect()
        }
    }

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            delay(3_000)
            mutex.withLock {
                reconnectJob = null
                if (!connectIntent || session?.isActive == true) return@withLock
                openSessionLocked()
            }
        }
    }

    private suspend fun handle(text: String) {
        val obj = try {
            json.parseToJsonElement(text).jsonObject
        } catch (e: Exception) {
            Log.w("App", "WS bad json: $text")
            return
        }
        when ((obj["type"] as? JsonPrimitive)?.contentOrNull) {
            "message" -> {
                val msg = ChatMessage(
                    id = obj.field("id"),
                    from = obj.field("from"),
                    to = obj.field("to"),
                    content = obj.field("content"),
                    createdAt = obj.field("createdAt")
                )
                _incoming.emit(msg)
            }
            "presence" -> {
                val userId = obj.field("userId")
                if (userId.isNotEmpty()) {
                    _presence.emit(
                        Presence(
                            userId = userId,
                            online = obj.field("status") == "online",
                            lastSeen = obj["lastSeen"]?.jsonPrimitive?.contentOrNull
                        )
                    )
                }
            }
            else -> Unit
        }
    }

    private fun JsonObject.field(name: String): String =
        this[name]?.jsonPrimitive?.contentOrNull ?: ""
}
