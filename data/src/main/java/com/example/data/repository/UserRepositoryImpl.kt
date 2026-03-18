package com.example.data.repository

import com.example.data.mapper.toEntity
import com.example.data.remote.api.user.UserApi
import com.example.data.remote.dto.request.user.UpdateProfileRequest
import com.example.domain.entity.UserProfile
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun getProfile(): Result<UserProfile> {
        return runCatching { userApi.getProfile() }
            .mapCatching {
                it.toEntity()
                    ?: throw IllegalArgumentException("Some fields in user object is null!")
            }
    }

    override suspend fun updateProfile(
        displayName: String?,
        phone: String?,
        bio: String?
    ): Result<UserProfile> {
        return runCatching {
            userApi.updateProfile(UpdateProfileRequest(displayName, phone, bio))
        }
            .mapCatching {
                it.toEntity()
                    ?: throw IllegalArgumentException("Some fields in user object is null!")
            }
    }
}