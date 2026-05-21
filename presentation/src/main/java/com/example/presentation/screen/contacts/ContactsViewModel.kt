package com.example.presentation.screen.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.Contact
import com.example.domain.entity.Presence
import com.example.domain.repository.PresenceRepository
import com.example.domain.usecase.contacts.AddContactUseCase
import com.example.domain.usecase.contacts.ListContactsUseCase
import com.example.domain.usecase.contacts.RemoveContactUseCase
import com.example.domain.usecase.messages.ConnectSocketUseCase
import com.example.presentation.mapper.toUserMessage
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
    private val presenceRepo: PresenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ContactsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // WS должен быть открыт для получения push-апдейтов presence
            connectSocket().onFailure { t -> Log.w("App", "WS connect failed: ${t.message}") }
        }
        observePresencePush()
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            list().fold(
                onSuccess = { items ->
                    _state.update { it.copy(items = items, loading = false) }
                    presenceRepo.subscribe(items.map { it.peerId })
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
            presenceRepo.unsubscribe(listOf(peerId))
        }
    }

    fun dismissAddError() {
        _state.update { it.copy(addError = null) }
    }

    private fun observePresencePush() {
        viewModelScope.launch {
            presenceRepo.updates.collect { p -> applyPresence(p) }
        }
    }

    private fun applyPresence(p: Presence) {
        _state.update { current ->
            val items = current.items.map { c ->
                if (c.peerId == p.userId) c.copy(presence = p) else c
            }
            current.copy(items = items)
        }
    }
}
