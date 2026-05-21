package com.example.presentation.screen.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.ChatMessage
import com.example.domain.entity.Contact
import com.example.domain.entity.LastMessagePreview
import com.example.domain.repository.TokenRepository
import com.example.domain.usecase.contacts.AddContactUseCase
import com.example.domain.usecase.contacts.ListContactsUseCase
import com.example.domain.usecase.contacts.RemoveContactUseCase
import com.example.domain.usecase.messages.ConnectSocketUseCase
import com.example.domain.usecase.messages.ObserveIncomingUseCase
import com.example.presentation.mapper.toUserMessage
import com.example.presentation.util.Jwt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsState(
    val items: List<Contact> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val addError: String? = null,
    val adding: Boolean = false
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val list: ListContactsUseCase,
    private val add: AddContactUseCase,
    private val remove: RemoveContactUseCase,
    private val connectSocket: ConnectSocketUseCase,
    private val observeIncoming: ObserveIncomingUseCase,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ContactsState())
    val state = _state.asStateFlow()

    @Volatile
    private var myUserId: String = ""

    init {
        viewModelScope.launch {
            myUserId = Jwt.extractSub(tokenRepository.getTokens()?.accessToken).orEmpty()
        }
        viewModelScope.launch {
            connectSocket().onFailure { t -> Log.w("App", "WS connect failed: ${t.message}") }
        }
        observeIncomingMessages()
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            list().fold(
                onSuccess = { items ->
                    _state.update { it.copy(items = items, loading = false) }
                },
                onFailure = { t ->
                    _state.update { it.copy(loading = false, error = t.toUserMessage()) }
                }
            )
        }
    }

    fun addContact(peerId: String) {
        val trimmed = peerId.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(adding = true, addError = null) }
            add(trimmed).fold(
                onSuccess = {
                    _state.update { it.copy(adding = false) }
                    load()
                },
                onFailure = { t ->
                    _state.update { it.copy(adding = false, addError = t.toUserMessage()) }
                }
            )
        }
    }

    fun removeContact(peerId: String) {
        viewModelScope.launch {
            remove(peerId).onSuccess { load() }
        }
    }

    fun dismissAddError() {
        _state.update { it.copy(addError = null) }
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            observeIncoming().collect { msg -> applyMessage(msg) }
        }
    }

    private fun applyMessage(msg: ChatMessage) {
        val peerId = if (msg.from == myUserId) msg.to else msg.from
        val preview = LastMessagePreview(
            content = msg.content,
            createdAt = msg.createdAt,
            fromMe = msg.from == myUserId
        )

        var foundContact = false
        _state.update { current ->
            val updated = current.items.map { c ->
                if (c.peerId == peerId) {
                    foundContact = true
                    c.copy(lastMessage = preview)
                } else c
            }
            if (!foundContact) return@update current

            // Перемещаем обновлённого собеседника в начало (как в Telegram)
            val moved = updated.partition { it.peerId == peerId }
            current.copy(items = moved.first + moved.second)
        }

        // Контакта нет в списке — сервер автодобавит, нам нужно перезагрузиться
        if (!foundContact) load()
    }
}
