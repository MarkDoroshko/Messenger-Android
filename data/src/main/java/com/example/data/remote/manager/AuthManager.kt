package com.example.data.remote.manager

import com.example.data.mapper.toBearerTokens
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.TokenRepository
import io.ktor.client.plugins.auth.providers.BearerTokens
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository
) {
    suspend fun getTokens(): BearerTokens? {
        return tokenRepository.getTokens()?.toBearerTokens()
    }

    suspend fun refreshAccessToken(refreshToken: String?): BearerTokens? {
        return authRepository.refresh(refreshToken ?: return null).fold(
            onSuccess = { it.toBearerTokens() },
            onFailure = { null }
        )
    }

    suspend fun saveTokens(tokens: BearerTokens) {
        tokenRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
    }
}