package com.example.domain.repository

import com.example.domain.entity.Tokens

interface TokenRepository {

    suspend fun getTokens(): Tokens?

    suspend fun saveTokens(accessToken: String, refreshToken: String?)

    suspend fun deleteTokens()
}