package com.example.data.util

object Constants {
    /**
     * LAN-IP компа с docker-стеком. Поменяй на свой реальный IP (`ipconfig` / `ifconfig`).
     * Телефон должен быть в той же Wi-Fi сети.
     */
    private const val HOST = "192.168.3.12"

    const val BASE_URL = "http://$HOST/api/v1"
    const val WS_URL = "ws://$HOST/ws"
}
