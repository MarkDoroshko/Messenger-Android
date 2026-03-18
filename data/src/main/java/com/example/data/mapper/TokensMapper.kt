package com.example.data.mapper

import com.example.data.remote.dto.response.auth.AuthResponse
import com.example.domain.entity.Tokens
import io.ktor.client.plugins.auth.providers.BearerTokens

fun AuthResponse.toTokens(): Tokens? {
    return Tokens(
        accessToken ?: return null,
        refreshToken ?: return null
    )
}

fun Tokens.toBearerTokens(): BearerTokens {
    return BearerTokens(accessToken, refreshToken)
}