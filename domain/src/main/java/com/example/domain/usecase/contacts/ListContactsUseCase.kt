package com.example.domain.usecase.contacts

import com.example.domain.entity.Contact
import com.example.domain.repository.ContactRepository
import javax.inject.Inject

class ListContactsUseCase @Inject constructor(
    private val repo: ContactRepository
) {
    suspend operator fun invoke(): Result<List<Contact>> = repo.list()
}
