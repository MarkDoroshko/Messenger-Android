package com.example.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.ChatMessage
import com.example.domain.repository.TokenRepository
import com.example.domain.usecase.messages.ConnectSocketUseCase
import com.example.domain.usecase.messages.GetHistoryUseCase
import com.example.domain.usecase.messages.ObserveIncomingUseCase
import com.example.domain.usecase.messages.SendMessageUseCase
import com.example.presentation.mapper.toUserMessage
import com.example.presentation.util.Jwt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val getHistory: GetHistoryUseCase,
    private val sendMessage: SendMessageUseCase,
    private val observeIncoming: ObserveIncomingUseCase,
    private val connectSocket: ConnectSocketUseCase
) : ViewModel() {

    val peerId: String = savedState.get<String>("peerId").orEmpty()

    private val _state = MutableStateFlow(ChatState(peerId = peerId))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val myId = Jwt.extractSub(tokenRepository.getTokens()?.accessToken).orEmpty()
            _state.update { it.copy(myUserId = myId) }
        }
        observeMessages()
        connectAndLoad()
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.InputDraft -> _state.update { it.copy(draft = intent.value) }
            ChatIntent.Send -> doSend()
            ChatIntent.Reload -> connectAndLoad()
        }
    }

    private fun connectAndLoad() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            connectSocket().fold(
                onSuccess = {},
                onFailure = { err ->
                    Log.e("App", "connect WS failed", err)
                    _state.update { s -> s.copy(error = err.toUserMessage()) }
                }
            )
            getHistory(peerId).fold(
                onSuccess = { items ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            messages = items.reversed()
                        )
                    }
                },
                onFailure = { err ->
                    Log.e("App", "history failed", err)
                    _state.update { s -> s.copy(isLoading = false, error = err.toUserMessage()) }
                }
            )
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            observeIncoming().collect { msg ->
                if (msg.from == peerId || msg.to == peerId) {
                    _state.update { it.copy(messages = it.messages + msg) }
                }
            }
        }
    }

    private fun doSend() {
        val text = _state.value.draft.trim()
        if (text.isEmpty()) return
        val cid = UUID.randomUUID().toString()
        viewModelScope.launch {
            // очищаем поле сразу; сообщение прилетит в observeMessages через incoming
            _state.update { it.copy(draft = "") }
            sendMessage(peerId, text, cid).onFailure { err ->
                Log.e("App", "send failed", err)
                _state.update { it.copy(error = err.toUserMessage(), draft = text) }
            }
        }
    }
}

data class ChatState(
    val peerId: String,
    val myUserId: String = "",
    val isLoading: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val error: String? = null
)

sealed interface ChatIntent {
    data class InputDraft(val value: String) : ChatIntent
    data object Send : ChatIntent
    data object Reload : ChatIntent
}
