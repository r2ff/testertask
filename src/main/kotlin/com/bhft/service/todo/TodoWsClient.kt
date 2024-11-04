package com.bhft.service.todo

import com.bhft.config.Config
import com.bhft.websocket.DefaultWebSocketClient
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object TodoWsClient {
    inline fun <reified T> connectWithFlow(): Flow<T> where T : Any =
        runBlocking {
            val socket =
                DefaultWebSocketClient.client.webSocketSession {
                    url("${Config.getConfig().todoWsUrl()}$WS")
                }

            socket
                .incoming
                .receiveAsFlow()
                .map { frame ->
                    if (frame is Frame.Text) {
                        try {
                            Json.decodeFromString<T>(frame.readText())
                        } catch (e: SerializationException) {
                            throw IllegalArgumentException("Failed to parse message: ${e.message}")
                        }
                    } else {
                        throw IllegalArgumentException("Received non-text frame")
                    }
                }.catch { error ->
                    when (error) {
                        is ClosedReceiveChannelException ->
                            throw IllegalStateException("Connection closed.")

                        else ->
                            throw IllegalStateException(error.localizedMessage ?: "Error")
                    }
                }
        }
}
