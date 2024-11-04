package com.bhft

import com.bhft.jupiter.annotation.GetTodo
import com.bhft.jupiter.extension.GetTodoExtension
import com.bhft.model.todo.TodoItem
import com.bhft.service.todo.todoClient
import io.ktor.http.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isTrue
import kotlin.random.Random

@DisplayName("Check PUT /todos")
@ExtendWith(GetTodoExtension::class)
class PutTodoTests {
    @Test
    fun `check success change todo list`(
        @GetTodo todoItem: TodoItem,
    ) {
        val newItem = todoItem.copy().apply { this.text = "New text ${this.id}" }
        todoClient.changeTodo(todoItem.id, newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(newItem)).isTrue()
            expectThat(this.body!!.contains(todoItem)).isFalse()
        }
    }

    @Test
    @Disabled("need to create bug report")
    fun `check change todo list without id`(
        @GetTodo todoItem: TodoItem,
    ) {
        val newItem = todoItem.copy().apply { this.id = null }
        todoClient.changeTodo(todoItem.id, newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.BadRequest)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(newItem)).isFalse()
            expectThat(this.body!!.contains(todoItem)).isTrue()
        }
    }

    @Test
    @Disabled("need to create bug report")
    fun `check change todo list with different ids`(
        @GetTodo todoItem: TodoItem,
    ) {
        val newItem = todoItem.copy().apply { this.id = Random.nextLong(0, Long.MAX_VALUE) }
        todoClient.changeTodo(todoItem.id, newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.BadRequest)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(newItem)).isFalse()
            expectThat(this.body!!.contains(todoItem)).isTrue()
        }
    }

    @Test
    fun `check change same todo list`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.changeTodo(todoItem.id, todoItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isTrue()
            expectThat(this.body!!.filter { it.id == todoItem.id }.size).isEqualTo(1)
        }
    }

    @Test
    @Disabled("need to create bug report")
    fun `check change with empty body`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.changeTodo(todoItem.id, TodoItem.Builder().build()).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.BadRequest)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isTrue()
            expectThat(this.body!!.filter { it.id == todoItem.id }.size).isEqualTo(1)
        }
    }
}
