package com.bhft.jupiter.extension

import com.bhft.service.todo.todoClient

class DeleteAllTodoTasksExtension : AroundTestExtension {
    override fun onEnd() {
        todoClient.deleteAllTodos()
    }
}
