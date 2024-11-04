package com.bhft.model.todo

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class TodoItem(
    var id: Long?,
    var text: String?,
    var completed: Boolean?,
) {
    class Builder {
        private var id: Long? = null
        private var text: String? = null
        private var completed: Boolean? = null

        fun id(id: Long?) = apply { id?.let { this.id = id } }

        fun text(text: String?) = apply { text?.let { this.text = text } }

        fun completed(completed: Boolean?) = apply { completed?.let { this.completed = completed } }

        fun build(): TodoItem = TodoItem(id, text, completed)
    }

    companion object {
        fun defaultTodoItem(): TodoItem {
            val id = Random.nextLong(0, Long.MAX_VALUE)
            return Builder()
                .id(id)
                .text("Generated default text field for item with id=$id")
                .completed(Random.nextBoolean())
                .build()
        }
    }
}
