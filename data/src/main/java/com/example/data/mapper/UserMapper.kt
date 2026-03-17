package com.example.data.mapper

import com.example.data.remote.dto.response.user.UserProfileResponse
import com.example.domain.entity.UserProfile

fun UserProfileResponse.toEntity(): UserProfile? {
    return UserProfile(
        displayName = displayName ?: return null,
        phone = phone ?: return null,
        bio = bio ?: return null
    )
}