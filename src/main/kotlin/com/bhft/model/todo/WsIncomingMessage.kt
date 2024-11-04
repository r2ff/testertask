package com.bhft.model.todo

import kotlinx.serialization.Serializable

@Serializable
data class WsIncomingMessage(
    val `data`: TodoItem,
    val type: String,
)
