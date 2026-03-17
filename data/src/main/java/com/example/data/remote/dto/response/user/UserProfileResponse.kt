package com.example.data.remote.dto.response.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    @SerialName("display_name")
    val displayName: String?,

    @SerialName("phone")
    val phone: String?,

    @SerialName("bio")
    val bio: String?
)
