package com.example.presentation.mapper

import com.example.domain.exception.AppHttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun Throwable.toUserMessage(): String {
    return when (this) {
        is ConnectException,
        is UnknownHostException -> "Нет подключения к серверу"

        is SocketTimeoutException -> "Сервер не отвечает. Попробуйте позже"

        is AppHttpException -> when (code) {
            400 -> "Неверный запрос. Проверьте введённые данные"
            401 -> "Неверный логин или пароль"
            403 -> "Доступ запрещён"
            404 -> "Данные не найдены"
            409 -> "Конфликт данных. Попробуйте ещё раз"
            422 -> "Некорректные данные. Проверьте введённые поля"
            429 -> "Слишком много запросов. Подождите немного"
            in 500..599 -> "Ошибка сервера. Попробуйте позже"
            else -> "Произошла ошибка. Попробуйте позже"
        }

        is IOException -> "Ошибка соединения. Проверьте интернет"

        else -> "Произошла ошибка. Попробуйте позже"
    }
}