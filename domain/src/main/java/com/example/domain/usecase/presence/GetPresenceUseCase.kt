package com.example.domain.usecase.presence

import com.example.domain.entity.Presence
import com.example.domain.repository.PresenceRepository
import javax.inject.Inject

class GetPresenceUseCase @Inject constructor(
    private val repo: PresenceRepository
) {
    suspend operator fun invoke(userId: String): Result<Presence> = repo.get(userId)
}
