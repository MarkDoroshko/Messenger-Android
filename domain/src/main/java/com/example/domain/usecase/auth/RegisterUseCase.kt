package com.example.domain.usecase.auth

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        phone: String,
        bio: String
    ): Result<Unit> {
        return authRepository.register(email, password, displayName, phone, bio)
    }
}