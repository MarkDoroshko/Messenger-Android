package com.example.domain.usecase.messages

import com.example.domain.entity.ChatMessage
import com.example.domain.repository.MessageRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    suspend operator fun invoke(peerId: String, limit: Int = 50): Result<List<ChatMessage>> =
        repo.getHistory(peerId, limit)
}
