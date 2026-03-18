package com.example.domain.usecase.user

import com.example.domain.entity.UserProfile
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return userRepository.getProfile()
    }
}