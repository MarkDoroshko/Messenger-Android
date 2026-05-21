package com.example.presentation.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.TokenRepository
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.user.GetProfileUseCase
import com.example.domain.usecase.user.UpdateProfileUseCase
import com.example.presentation.mapper.toUserMessage
import com.example.presentation.util.Jwt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state = _state.asStateFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> load()
            ProfileIntent.Update -> update()
            ProfileIntent.Logout -> viewModelScope.launch { logoutUseCase() }
            is ProfileIntent.InputDisplayName -> updateField { it.copy(displayName = intent.value) }
            is ProfileIntent.InputPhone -> updateField { it.copy(phone = intent.value) }
            is ProfileIntent.InputBio -> updateField { it.copy(bio = intent.value) }
        }
    }

    private fun load() {
        _state.value = ProfileState.Loading
        viewModelScope.launch {
            val userId = Jwt.extractSub(tokenRepository.getTokens()?.accessToken).orEmpty()
            getProfileUseCase().fold(
                onSuccess = { p ->
                    _state.value = ProfileState.Data(
                        displayName = p.displayName,
                        phone = p.phone,
                        bio = p.bio,
                        userId = userId
                    )
                },
                onFailure = {
                    Log.e("App", it.toString())
                    _state.value = ProfileState.Error(it.toUserMessage())
                }
            )
        }
    }

    private fun update() {
        val current = _state.value as? ProfileState.Data ?: return
        viewModelScope.launch {
            updateProfileUseCase(current.displayName, current.phone, current.bio).fold(
                onSuccess = { p ->
                    _state.value = current.copy(
                        displayName = p.displayName,
                        phone = p.phone,
                        bio = p.bio
                    )
                },
                onFailure = {
                    Log.e("App", it.toString())
                    _state.value = ProfileState.Error(it.toUserMessage())
                }
            )
        }
    }

    private inline fun updateField(transform: (ProfileState.Data) -> ProfileState.Data) {
        val current = _state.value as? ProfileState.Data ?: return
        _state.value = transform(current)
    }
}

sealed interface ProfileState {
    data object Loading : ProfileState
    data class Data(
        val displayName: String,
        val phone: String,
        val bio: String,
        val userId: String
    ) : ProfileState
    data class Error(val error: String?) : ProfileState
}

sealed interface ProfileIntent {
    data object Load : ProfileIntent
    data object Update : ProfileIntent
    data object Logout : ProfileIntent
    data class InputDisplayName(val value: String) : ProfileIntent
    data class InputPhone(val value: String) : ProfileIntent
    data class InputBio(val value: String) : ProfileIntent
}
