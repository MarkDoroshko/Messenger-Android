package com.example.data.remote.dto.request.contacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddContactRequest(
    @SerialName("peer_id") val peerId: String
)
