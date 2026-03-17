package com.example.data.remote.api.auth

import com.example.data.remote.dto.request.auth.LoginRequest
import com.example.data.remote.dto.request.auth.RegisterRequest
import com.example.data.remote.dto.response.auth.AuthResponse

interface AuthApi {
    suspend fun login(credentials: LoginRequest): AuthResponse

    suspend fun register(userData: RegisterRequest): AuthResponse

    suspend fun refresh(refreshToken: String): AuthResponse
}