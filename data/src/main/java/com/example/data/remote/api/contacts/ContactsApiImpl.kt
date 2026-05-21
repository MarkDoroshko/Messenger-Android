package com.example.data.remote.api.contacts

import com.example.data.remote.dto.request.contacts.AddContactRequest
import com.example.data.remote.dto.response.contacts.ContactsResponse
import com.example.data.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class ContactsApiImpl @Inject constructor(
    private val client: HttpClient
) : ContactsApi {
    override suspend fun list(): ContactsResponse =
        client.get("${Constants.BASE_URL}/contacts").body()

    override suspend fun add(peerId: String) {
        client.post("${Constants.BASE_URL}/contacts") {
            contentType(ContentType.Application.Json)
            setBody(AddContactRequest(peerId))
        }
    }

    override suspend fun remove(peerId: String) {
        client.delete("${Constants.BASE_URL}/contacts/$peerId")
    }
}
