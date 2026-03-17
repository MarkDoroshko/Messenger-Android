package com.example.domain.repository

import com.example.domain.entity.UserProfile

interface UserRepository {
    suspend fun getProfile(): Result<UserProfile>

    suspend fun updateProfile(displayName: String?, phone: String?, bio: String?): Result<UserProfile>
}