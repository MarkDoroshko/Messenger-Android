package com.example.presentation.screen.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.auth.RegisterUseCase
import com.example.presentation.mapper.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun processIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.Input -> {
                _state.update { previousState ->
                    when (intent.typeField) {
                        TypeField.EMAIL -> previousState.copy(email = intent.value)
                        TypeField.PASSWORD -> previousState.copy(password = intent.value)
                        TypeField.DISPLAY_NAME -> previousState.copy(displayName = intent.value)
                        TypeField.PHONE -> previousState.copy(phone = intent.value)
                        TypeField.BIO -> previousState.copy(bio = intent.value)
                    }
                }
            }

            RegisterIntent.Submit -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        registerUseCase(
                            email = previousState.email,
                            password = previousState.password,
                            displayName = previousState.displayName,
                            phone = previousState.phone,
                            bio = previousState.bio
                        ).fold(
                            onSuccess = { previousState },
                            onFailure = {
                                Log.e("App", it.toString())
                                previousState.copy(error = it.toUserMessage())
                            }
                        )
                    }
                }
            }

            RegisterIntent.ChangePasswordVisibility -> {
                _state.update { previousState ->
                    previousState.copy(passwordVisibility = !previousState.passwordVisibility)
                }
            }
        }
    }
}

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val phone: String = "",
    val bio: String = "",
    val passwordVisibility: Boolean = false,
    val error: String? = null
) {
    val isSubmitButtonEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && displayName.isNotBlank() && phone.isNotBlank() && bio.isNotBlank()
}

sealed interface RegisterIntent {
    data class Input(
        val typeField: TypeField,
        val value: String
    ) : RegisterIntent

    data object ChangePasswordVisibility : RegisterIntent

    data object Submit : RegisterIntent
}

enum class TypeField { EMAIL, PASSWORD, DISPLAY_NAME, PHONE, BIO }