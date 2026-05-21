package com.example.data.remote.api.contacts

import com.example.data.remote.dto.response.contacts.ContactsResponse

interface ContactsApi {
    suspend fun list(): ContactsResponse
    suspend fun add(peerId: String)
    suspend fun remove(peerId: String)
}
