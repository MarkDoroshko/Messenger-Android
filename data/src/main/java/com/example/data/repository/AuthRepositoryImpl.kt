package com.example.data.repository

import com.example.data.mapper.toTokens
import com.example.data.remote.api.auth.AuthApi
import com.example.data.remote.dto.request.auth.LoginRequest
import com.example.data.remote.dto.request.auth.RegisterRequest
import com.example.domain.entity.Tokens
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.TokenRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenRepository: TokenRepository
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching { authApi.login(LoginRequest(email, password)) }
            .mapCatching {
                val tokens = it.toTokens() ?: throw IllegalArgumentException("Some token is null!")
                tokenRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
            }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String,
        phone: String,
        bio: String
    ): Result<Unit> {
        return runCatching {
            authApi.register(RegisterRequest(email, password, displayName, phone, bio))
        }.mapCatching {
            val tokens = it.toTokens() ?: throw IllegalArgumentException("Some token is null!")
            tokenRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
        }
    }

    override suspend fun refresh(refreshToken: String): Result<Tokens> {
        return runCatching { authApi.refresh(refreshToken) }
            .mapCatching {
                val tokens = it.toTokens() ?: throw IllegalArgumentException("Some token is null!")
                tokenRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
                tokens
            }
    }
}