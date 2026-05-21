package com.example.data.di

import com.example.data.remote.manager.AuthManager
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.TokenRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.domain.exception.AppHttpException
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false  // Исключение из тела ответа nullable-полей
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        authManager: AuthManager,
        json: Json
    ): HttpClient {
        return HttpClient(CIO) {
            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value
                    if (statusCode >= 400) {
                        throw AppHttpException(statusCode)
                    }
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(WebSockets) {
                // Keepalive поверх Wi-Fi/NAT. Сервер дополнительно пингует каждые 30с.
                pingIntervalMillis = 20_000L
                maxFrameSize = Long.MAX_VALUE
            }

            install(Logging) {
                level = LogLevel.INFO

                sanitizeHeader { header ->
                    header.equals(
                        HttpHeaders.Authorization,
                        ignoreCase = true
                    )
                }
            }

            install(HttpRequestRetry) {
                maxRetries = 5
                retryIf { request, response ->
                    !request.url.encodedPath.contains("/auth") &&
                            response.status.value in 500..599
                }
                exponentialDelay()
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        authManager.getTokens()
                    }
                    refreshTokens {
                        val currentTokens = this.oldTokens ?: return@refreshTokens null

                        val newTokens = authManager.refreshAccessToken(
                            currentTokens.refreshToken ?: return@refreshTokens null
                        )
                            ?: return@refreshTokens null

                        authManager.saveTokens(newTokens)

                        newTokens
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideAuthManager(
        tokenRepository: TokenRepository,
        authRepository: Lazy<AuthRepository>
    ): AuthManager = AuthManager(tokenRepository, authRepository)
}