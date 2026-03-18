package com.example.domain.repository

import com.example.domain.entity.Tokens

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        phone: String,
        bio: String
    ): Result<Unit>

    suspend fun refresh(refreshToken: String): Result<Tokens>
}