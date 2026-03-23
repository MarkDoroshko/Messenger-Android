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
            is ProfileIntent.Input -> {
                _state.update { previousState ->
                    if (previousState.content != null && previousState.content is ProfileContent.Loaded) {
                        when (intent.typeField) {
                            TypeField.DISPLAY_NAME -> previousState.copy(
                                content = previousState.content.copy(
                                    displayName = intent.value
                                )
                            )

                            TypeField.PHONE -> previousState.copy(
                                content = previousState.content.copy(
                                    phone = intent.value
                                )
                            )

                            TypeField.BIO -> previousState.copy(
                                content = previousState.content.copy(
                                    bio = intent.value
                                )
                            )
                        }
                    } else previousState
                }
            }

            ProfileIntent.Load -> {
                _state.value = _state.value.copy(isLoading = true)

                viewModelScope.launch {
                    getProfileUseCase().fold(
                        onSuccess = { userProfile ->
                            _state.value = _state.value.copy(isLoading = false)

                            _state.value = _state.value.copy(
                                content = ProfileContent.Loaded(
                                    displayName = userProfile.displayName,
                                    phone = userProfile.phone,
                                    bio = userProfile.bio
                                )
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

data class ProfileState(
    val content: ProfileContent? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ProfileContent {
    data class Loaded(
        val displayName: String,
        val phone: String,
        val bio: String
    ) : ProfileContent

    data object Empty : ProfileContent
}

sealed interface ProfileIntent {
    data object Load : ProfileIntent

    data object Update : ProfileIntent

    data class Input(
        val typeField: TypeField,
        val value: String
    ) : ProfileIntent
}

enum class TypeField { DISPLAY_NAME, PHONE, BIO }