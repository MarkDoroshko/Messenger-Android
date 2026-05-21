package com.example.domain.usecase.messages

import com.example.domain.entity.ChatMessage
import com.example.domain.repository.MessageRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ObserveIncomingUseCase @Inject constructor(
    private val repo: MessageRepository
) {
    operator fun invoke(): SharedFlow<ChatMessage> = repo.incoming
}
