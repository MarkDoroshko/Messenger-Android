package com.example.data.remote.api.auth

import com.example.data.remote.dto.request.auth.LoginRequest
import com.example.data.remote.dto.request.auth.RegisterRequest
import com.example.data.remote.dto.response.auth.AuthResponse
import com.example.data.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class AuthApiImpl @Inject constructor(
    private val client: HttpClient
) : AuthApi {
    override suspend fun login(credentials: LoginRequest): AuthResponse {
        return client.post("${Constants.BASE_URL}/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.body<AuthResponse>()
    }

    override suspend fun register(userData: RegisterRequest): AuthResponse {
        return client.post("${Constants.BASE_URL}/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(userData)
        }.body<AuthResponse>()
    }

    override suspend fun refresh(refreshToken: String): AuthResponse {
        return client.post("${Constants.BASE_URL}/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(refreshToken)
        }.body<AuthResponse>()
    }
}