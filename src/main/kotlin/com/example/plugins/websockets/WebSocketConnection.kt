package com.example.plugins.websockets

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class WebSocketConnection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}