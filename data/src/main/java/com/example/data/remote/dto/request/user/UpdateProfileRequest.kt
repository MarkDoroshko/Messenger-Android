package com.example.data.remote.dto.request.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("display_name")
    val displayName: String? = null,

    @SerialName("phone")
    val phone: String? = null,

    @SerialName("bio")
    val bio: String? = null
)
