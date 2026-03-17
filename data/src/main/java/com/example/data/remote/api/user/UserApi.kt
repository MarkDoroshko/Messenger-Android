package com.example.data.remote.api.user

import com.example.data.remote.dto.request.user.UpdateProfileRequest
import com.example.data.remote.dto.response.user.UserProfileResponse

interface UserApi {
    suspend fun getProfile(): UserProfileResponse

    suspend fun updateProfile(updatedProfile: UpdateProfileRequest): UserProfileResponse
}