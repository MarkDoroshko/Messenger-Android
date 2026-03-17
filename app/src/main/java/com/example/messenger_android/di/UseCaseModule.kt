package com.example.messenger_android.di

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.TokenRepository
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.auth.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        authRepository: AuthRepository
    ): RegisterUseCase = RegisterUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        tokenRepository: TokenRepository
    ): LogoutUseCase = LogoutUseCase(tokenRepository)
}