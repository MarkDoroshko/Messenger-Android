package com.example.data.repository

import com.example.data.mapper.toEntity
import com.example.data.remote.api.contacts.ContactsApi
import com.example.domain.entity.Contact
import com.example.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val api: ContactsApi
) : ContactRepository {
    override suspend fun list(): Result<List<Contact>> = runCatching {
        api.list().items.map { it.toEntity() }
    }

    override suspend fun add(peerId: String): Result<Unit> = runCatching { api.add(peerId) }

    override suspend fun remove(peerId: String): Result<Unit> = runCatching { api.remove(peerId) }
}
