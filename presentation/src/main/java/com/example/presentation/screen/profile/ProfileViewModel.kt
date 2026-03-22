package com.example.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.user.GetProfileUseCase
import com.example.domain.usecase.user.UpdateProfileUseCase
import com.example.presentation.mapper.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state = _state.asStateFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.InputBio -> {
                _state.update { previousState ->
                    if (previousState is ProfileState.Data) {
                        previousState.copy(bio = intent.query)
                    } else previousState
                }
            }

            is ProfileIntent.InputDisplayName -> {
                _state.update { previousState ->
                    if (previousState is ProfileState.Data) {
                        previousState.copy(displayName = intent.query)
                    } else previousState
                }
            }

            is ProfileIntent.InputPhone -> {
                _state.update { previousState ->
                    if (previousState is ProfileState.Data) {
                        previousState.copy(phone = intent.query)
                    } else previousState
                }
            }

            ProfileIntent.Load -> {
                viewModelScope.launch {
                    getProfileUseCase().fold(
                        onSuccess = { userProfile ->

                        },
                        onFailure = { _state.value = ProfileState.Error(it.toUserMessage()) }
                    )
                }
            }

            ProfileIntent.Update -> {

            }
        }
    }
}

sealed interface ProfileState {
    data class Data(
        val displayName: String,
        val phone: String,
        val bio: String
    ) : ProfileState

    data object Loading : ProfileState

    data class Error(
        val error: String? = null
    ) : ProfileState
}

sealed interface ProfileIntent {
    data object Load : ProfileIntent

    data object Update : ProfileIntent

    data class InputDisplayName(val query: String) : ProfileIntent

    data class InputPhone(val query: String) : ProfileIntent

    data class InputBio(val query: String) : ProfileIntent
}