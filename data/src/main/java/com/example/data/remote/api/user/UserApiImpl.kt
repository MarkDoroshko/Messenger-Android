package com.example.data.remote.api.user

import com.example.data.remote.dto.request.user.UpdateProfileRequest
import com.example.data.remote.dto.response.user.UserProfileResponse
import com.example.data.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class UserApiImpl @Inject constructor(
    private val client: HttpClient
) : UserApi {
    override suspend fun getProfile(): UserProfileResponse {
        return client.get("${Constants.BASE_URL}/profile").body<UserProfileResponse>()
    }

    override suspend fun updateProfile(updatedProfile: UpdateProfileRequest): UserProfileResponse {
        return client.patch("${Constants.BASE_URL}/profile") {
            contentType(ContentType.Application.Json)
            setBody(updatedProfile)
        }.body<UserProfileResponse>()
    }
}