package com.example.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.domain.entity.Tokens
import com.example.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenRepository {
    override suspend fun getTokens(): Tokens? {
        return dataStore.data.firstOrNull()?.let { preferences ->
            Tokens(
                accessToken = preferences[ACCESS_TOKEN_KEY] ?: return@let null,
                refreshToken = preferences[REFRESH_TOKEN_KEY]
            )
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            if (refreshToken != null) preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    override suspend fun deleteTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] != null && preferences[REFRESH_TOKEN_KEY] != null
        }
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_key")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_key")
    }
}