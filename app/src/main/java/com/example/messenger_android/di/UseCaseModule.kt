package com.example.messenger_android.di

import com.example.domain.repository.AuthRepository
import com.example.domain.repository.TokenRepository
import com.example.domain.repository.UserRepository
import com.example.domain.usecase.auth.CheckLoggedUseCase
import com.example.domain.usecase.auth.LoginUseCase
import com.example.domain.usecase.auth.LogoutUseCase
import com.example.domain.usecase.auth.RegisterUseCase
import com.example.domain.usecase.user.GetProfileUseCase
import com.example.domain.usecase.user.UpdateProfileUseCase
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

    @Provides
    @Singleton
    fun provideGetProfileUseCase(
        userRepository: UserRepository
    ): GetProfileUseCase = GetProfileUseCase(userRepository)

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(
        userRepository: UserRepository
    ): UpdateProfileUseCase = UpdateProfileUseCase(userRepository)

    @Provides
    @Singleton
    fun provideCheckLoggedUseCase(
        tokenRepository: TokenRepository
    ): CheckLoggedUseCase = CheckLoggedUseCase(tokenRepository)
}