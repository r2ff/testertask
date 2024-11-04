package com.bhft

import com.bhft.model.todo.TodoItem
import com.bhft.service.todo.todoClient
import io.ktor.http.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Isolated
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@DisplayName("Check GET /todos")
@Isolated
class GetTodoTests {
    companion object {
        private const val TODO_COUNT = 10
        private val TODO_LIST = mutableListOf<TodoItem>()

        @JvmStatic
        @BeforeAll
        fun setup() {
            todoClient.deleteAllTodos()
            repeat(TODO_COUNT) {
                val todoItem = TodoItem.defaultTodoItem()
                todoClient.createTodo(todoItem).also { TODO_LIST.add(todoItem) }
            }
        }
    }

    @Test
    fun `check get full todo list`() {
        todoClient.getTodoList().apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
            expectThat(this.body?.size).isNotNull().isEqualTo(TODO_COUNT)
            expectThat(this.body).isEqualTo(TODO_LIST)
        }
    }

    @Test
    fun `check get todo list with limit`() {
        val limit = 5
        todoClient.getTodoList(limit = 5).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
            expectThat(this.body?.size).isNotNull().isEqualTo(limit)
            expectThat(this.body).isEqualTo(TODO_LIST.subList(0, limit))
        }
    }

    @Test
    fun `check get todo list with offset`() {
        val offset = 2
        todoClient.getTodoList(offset = offset).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
            expectThat(this.body?.size).isNotNull().isEqualTo(TODO_COUNT - offset)
            expectThat(this.body).isEqualTo(TODO_LIST.subList(offset, TODO_COUNT))
        }
    }

    @Test
    fun `check get todo list with offset and limit`() {
        val offset = 2
        val limit = 5
        todoClient.getTodoList(2, 5).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
            expectThat(this.body?.size).isNotNull().isEqualTo(limit)
            expectThat(this.body).isEqualTo(TODO_LIST.subList(offset, offset + limit))
        }
    }

    @Test
    fun `check get todo list with zero limit`() {
        todoClient.getTodoList(limit = 0).apply {
            expectThat(this.status).isEqualTo(HttpStatusCode.OK)
            expectThat(this.body).isNotNull().isEmpty()
        }
    }

    /*
    TODO
    check list:
    - check get todolist with invalid limit (negative)
    - check get todolist with invalid offset (negative)
    - check get todolist, when todos doesn't exist
    - check get todolist when offset is greater than limit
     */
}
