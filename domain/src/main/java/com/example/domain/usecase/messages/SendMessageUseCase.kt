package com.example.domain.usecase.messages

import com.example.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    suspend operator fun invoke(toUserId: String, content: String, clientMessageId: String): Result<Unit> =
        repo.send(toUserId, content, clientMessageId)
}
