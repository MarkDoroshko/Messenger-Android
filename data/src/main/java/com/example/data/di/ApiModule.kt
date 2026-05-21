package com.example.data.di

import com.example.data.remote.api.auth.AuthApi
import com.example.data.remote.api.auth.AuthApiImpl
import com.example.data.remote.api.contacts.ContactsApi
import com.example.data.remote.api.contacts.ContactsApiImpl
import com.example.data.remote.api.messages.MessagesApi
import com.example.data.remote.api.messages.MessagesApiImpl
import com.example.data.remote.api.user.UserApi
import com.example.data.remote.api.user.UserApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ApiModule {
    @Binds
    @Singleton
    fun bindAuthApi(impl: AuthApiImpl): AuthApi

    @Binds
    @Singleton
    fun bindUserApi(impl: UserApiImpl): UserApi

    @Binds
    @Singleton
    fun bindMessagesApi(impl: MessagesApiImpl): MessagesApi

    @Binds
    @Singleton
    fun bindContactsApi(impl: ContactsApiImpl): ContactsApi
}
