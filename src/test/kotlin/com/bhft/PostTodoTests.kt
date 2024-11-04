package com.bhft

import com.bhft.model.todo.TodoItem
import com.bhft.service.todo.todoClient
import io.ktor.http.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNullOrEmpty
import strikt.assertions.isTrue

@DisplayName("Check POST /todos")
class PostTodoTests {
    companion object {
        @JvmStatic
        fun forbiddenTodoItemsProvider() =
            listOf(
                TodoItem.defaultTodoItem().apply { this.id = null },
                TodoItem.defaultTodoItem().apply { this.text = null },
                TodoItem.defaultTodoItem().apply { this.completed = null },
            )
    }

    @Test
    fun `check create new valid todo list`() {
        val newItem = TodoItem.defaultTodoItem()
        todoClient.createTodo(newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.Created)
            expectThat(this.body).isNullOrEmpty()
        }

        val createdItem =
            todoClient.getTodoList().body?.filter {
                it.id == newItem.id
            }
        expectThat(createdItem).isNotNull()
        expectThat(createdItem?.size).isEqualTo(1)
        expectThat(createdItem?.first()).isEqualTo(newItem)
    }

    @Test
    fun `check create same todo list`() {
        val newItem = TodoItem.defaultTodoItem()
        todoClient.createTodo(newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.Created)
            expectThat(this.body).isNullOrEmpty()
        }

        val createdItem =
            todoClient.getTodoList().body?.filter {
                it.id == newItem.id
            }
        expectThat(createdItem).isNotNull()
        expectThat(createdItem?.size).isEqualTo(1)
        expectThat(createdItem?.first()).isEqualTo(newItem)

        todoClient.createTodo(newItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.BadRequest)
            expectThat(this.body).isNullOrEmpty()
        }

        val createdItemAfter =
            todoClient.getTodoList().body?.filter {
                it.id == newItem.id
            }
        expectThat(createdItemAfter).isNotNull()
        expectThat(createdItemAfter?.size).isEqualTo(1)
        expectThat(createdItemAfter?.first()).isEqualTo(newItem)
    }

    @ParameterizedTest
    @MethodSource("forbiddenTodoItemsProvider")
    fun `create todo list without necessary field`(todoItem: TodoItem) {
        todoClient.createTodo(todoItem).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.BadRequest)
            expectThat(this.error!!.contains("Request body deserialize error")).isTrue()
        }
    }

    /*
    TODO checklist:
    - check create todolist with negative id
    - check create todolist with empty text field
    - check create todolist with MAX_SIZE text field (depends on requirements)
    - check create todolist with invalid completed field
    - check create todolist with some addition value
    - check create todolist with empty body
    - check create todolist with custom headers
     */
}
