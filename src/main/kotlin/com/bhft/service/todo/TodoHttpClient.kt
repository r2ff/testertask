package com.bhft.service.todo

import com.bhft.config.Config
import com.bhft.extension.authorization
import com.bhft.http.DefaultHttpClient
import com.bhft.http.wrapResponse
import com.bhft.model.todo.TodoItem
import io.ktor.client.request.*
import io.ktor.http.*
import io.qameta.allure.Step
import kotlinx.coroutines.runBlocking

typealias todoClient = TodoHttpClient

object TodoHttpClient {
    private val client =
        DefaultHttpClient(
            Config.getConfig().todoUrl(),
            "todo-client",
        )

    @Step("Get TODO list")
    fun getTodoList(offset: Int? = null, limit: Int? = null) =
        runBlocking {
            client
                .get(TODO) {
                    parameters {
                        offset?.let { parameter("offset", offset) }
                        limit?.let { parameter("limit", limit) }
                    }
                }.wrapResponse<List<TodoItem>>()
        }

    @Step("Create TODO")
    fun createTodo(body: TodoItem) =
        runBlocking {
            client
                .post(TODO) {
                    setBody(body)
                }.wrapResponse<String>()
        }

    @Step("Create TODO")
    fun createTodo(
        id: Long? = null,
        text: String? = null,
        completed: Boolean? = null,
    ) = runBlocking {
        client
            .post(TODO) {
                setBody(
                    TodoItem
                        .Builder()
                        .id(id)
                        .text(text)
                        .completed(completed)
                        .build(),
                )
            }.wrapResponse<String>()
    }

    @Step("Delete TODO {id}")
    fun deleteTodo(
        id: Long?,
        login: String? = Config.getConfig().userLogin(),
        password: String? = Config.getConfig().userPassword(),
    ) = runBlocking {
        client
            .delete("${TODO}${SEPARATOR}$id") {
                authorization(login, password)
            }.wrapResponse<String>()
    }

    @Step("Delete all TODOs")
    fun deleteAllTodos() {
        getTodoList()
            .body
            ?.map {
                it.id
            }?.forEach {
                deleteTodo(it)
            }
    }

    @Step("Change TODO id={id}")
    fun changeTodo(id: Long?, todoItem: TodoItem) =
        runBlocking {
            client
                .put("$TODO$SEPARATOR$id") {
                    setBody(todoItem)
                }.wrapResponse<String>()
        }
}
