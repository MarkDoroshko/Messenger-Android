package com.example.presentation.screen.profile

import android.util.Log
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
                            _state.value = ProfileState.Data(
                                displayName = userProfile.displayName,
                                phone = userProfile.phone,
                                bio = userProfile.bio
                            )
                        },
                        onFailure = {
                            Log.e("App", it.toString())
                            _state.value = ProfileState.Error(it.toUserMessage())
                        }
                    )
                }
            }

            ProfileIntent.Update -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is ProfileState.Data) {
                            updateProfileUseCase(
                                previousState.displayName,
                                previousState.phone,
                                previousState.bio
                            ).fold(
                                onSuccess = { userProfile ->
                                    ProfileState.Data(
                                        displayName = userProfile.displayName,
                                        phone = userProfile.phone,
                                        bio = userProfile.bio
                                    )
                                },
                                onFailure = {
                                    Log.e("App", it.toString())
                                    ProfileState.Error(it.toUserMessage())
                                }
                            )
                        } else previousState
                    }
                }
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