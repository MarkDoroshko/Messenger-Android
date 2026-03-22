package com.example.domain.usecase.auth

import com.example.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckLoggedUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return tokenRepository.isLoggedIn()
    }
}