package com.bhft.websocket

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object DefaultWebSocketClient {
    val client =
        HttpClient(OkHttp) {
            engine {
                preconfigured =
                    OkHttpClient
                        .Builder()
                        .pingInterval(20, TimeUnit.SECONDS)
                        .build()
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
}
