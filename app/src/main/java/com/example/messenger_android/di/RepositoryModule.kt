package com.example.messenger_android.di

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.ContactRepositoryImpl
import com.example.data.repository.MessageRepositoryImpl
import com.example.data.repository.PresenceRepositoryImpl
import com.example.data.repository.TokenRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.ContactRepository
import com.example.domain.repository.MessageRepository
import com.example.domain.repository.PresenceRepository
import com.example.domain.repository.TokenRepository
import com.example.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    fun bindPresenceRepository(impl: PresenceRepositoryImpl): PresenceRepository

    @Binds
    @Singleton
    fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository
}
