package com.example.domain.exception

import java.io.IOException

class AppHttpException(
    val code: Int,
    override val message: String = "HTTP $code"
) : IOException(message)
