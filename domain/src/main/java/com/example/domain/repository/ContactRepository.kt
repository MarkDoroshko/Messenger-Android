package com.example.domain.repository

import com.example.domain.entity.Contact

interface ContactRepository {
    suspend fun list(): Result<List<Contact>>
    suspend fun add(peerId: String): Result<Unit>
    suspend fun remove(peerId: String): Result<Unit>
}
