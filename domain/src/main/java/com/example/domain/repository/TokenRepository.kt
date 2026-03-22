package com.example.domain.repository

import com.example.domain.entity.Tokens
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun getTokens(): Tokens?

    suspend fun saveTokens(accessToken: String, refreshToken: String?)

    suspend fun deleteTokens()

    fun isLoggedIn(): Flow<Boolean>
}