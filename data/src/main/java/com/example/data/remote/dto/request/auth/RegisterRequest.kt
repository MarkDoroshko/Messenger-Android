package com.example.data.remote.dto.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("phone")
    val phone: String,

    @SerialName("bio")
    val bio: String
)
