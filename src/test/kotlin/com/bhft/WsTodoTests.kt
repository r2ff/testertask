package com.bhft

import com.bhft.model.todo.TodoItem
import com.bhft.model.todo.WsIncomingMessage
import com.bhft.observer.FlowObserver
import com.bhft.service.todo.TodoWsClient
import com.bhft.service.todo.todoClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Isolated
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEmpty

@Isolated
@DisplayName("Check WS /ws")
class WsTodoTests {
    @Test
    fun `check websocket returned information about new todo list`() {
        val observer =
            FlowObserver(
                TodoWsClient.connectWithFlow<WsIncomingMessage>(),
            )

        val item = TodoItem.defaultTodoItem()
        todoClient.createTodo(item)
        observer
            .findNext {
                println(it)
                item.id == it.data.id
            }.apply {
                expectThat(this).isNotEmpty()
                expectThat(this.last().data).isEqualTo(item)
                expectThat(this.last().type).isEqualTo("new_todo")
            }

        val changedItem = item.copy().apply { this.text = "Edited text" }
        todoClient.changeTodo(changedItem.id, changedItem)
        observer
            .findNext {
                it.data.id == changedItem.id
            }.apply {
                expectThat(this).isEmpty()
            }
        todoClient.deleteTodo(changedItem.id)
        observer
            .findNext {
                it.data.id == changedItem.id
            }.apply {
                expectThat(this).isEmpty()
            }
    }
}
