package com.example.domain.usecase.messages

import com.example.domain.repository.MessageRepository
import javax.inject.Inject

class DisconnectSocketUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    suspend operator fun invoke() = repo.disconnect()
}
