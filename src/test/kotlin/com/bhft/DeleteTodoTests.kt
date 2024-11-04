package com.bhft

import com.bhft.jupiter.annotation.GetTodo
import com.bhft.jupiter.extension.GetTodoExtension
import com.bhft.model.todo.TodoItem
import com.bhft.service.todo.todoClient
import io.ktor.http.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

@DisplayName("Check DELETE /todos")
@ExtendWith(GetTodoExtension::class)
class DeleteTodoTests {
    @Test
    fun `check success delete todo list`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.deleteTodo(todoItem.id).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.NoContent)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isFalse()
        }
    }

    @Test
    fun `check delete todo list without authorization`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.deleteTodo(todoItem.id, null, null).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.Unauthorized)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isTrue()
        }
    }

    @Test
    fun `check delete todo list with wrong password`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.deleteTodo(todoItem.id, password = "fakePassword1").apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.Unauthorized)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isTrue()
        }
    }

    @Test
    fun `check delete todo list with empty password`(
        @GetTodo todoItem: TodoItem,
    ) {
        todoClient.deleteTodo(todoItem.id, password = "").apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.Unauthorized)
        }

        todoClient.getTodoList().apply {
            expectThat(this.body).isNotNull()
            expectThat(this.body!!.contains(todoItem)).isTrue()
        }
    }

    @Test
    fun `check delete todo list with non existent id`() {
        todoClient.deleteTodo(6666).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    /*
    TODO checklist:
    - check delete todolist with another user
    - check delete todolist with forbidden id
    - check delete todolist second time
     */
}
