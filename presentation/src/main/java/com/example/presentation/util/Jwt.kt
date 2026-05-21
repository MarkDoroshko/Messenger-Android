package com.example.presentation.util

import android.util.Base64
import org.json.JSONObject

object Jwt {
    /** Декодирует payload JWT и возвращает значение `sub` (userId). null, если не распарсилось. */
    fun extractSub(accessToken: String?): String? {
        if (accessToken.isNullOrBlank()) return null
        val parts = accessToken.split('.')
        if (parts.size < 2) return null
        return try {
            val bytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val obj = JSONObject(String(bytes))
            obj.optString("sub").takeIf { it.isNotBlank() }
        } catch (e: Throwable) {
            null
        }
    }
}
