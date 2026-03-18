package com.example.domain.usecase.user

import com.example.domain.entity.UserProfile
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        displayName: String?,
        phone: String?,
        bio: String?
    ): Result<UserProfile> {
        return userRepository.updateProfile(displayName, phone, bio)
    }
}