package com.bhft.jupiter.extension

import com.bhft.service.todo.todoClient

/**
 * JUnit Jupiter extension that handles cleanup of todotasks after test execution.
 * This extension automatically removes all todotasks from the system when tests complete.
 */

class DeleteAllTodoTasksExtension : AroundTestExtension {
    override fun onEnd() {
        todoClient.deleteAllTodos()
    }
}
