package com.example.domain.usecase.contacts

import com.example.domain.repository.ContactRepository
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val repo: ContactRepository
) {
    suspend operator fun invoke(peerId: String): Result<Unit> = repo.add(peerId)
}
