package com.bhft.jupiter.extension

import com.bhft.jupiter.annotation.GetTodo
import com.bhft.model.todo.TodoItem
import com.bhft.service.todo.todoClient
import io.ktor.http.*
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import strikt.api.expectThat
import strikt.assertions.isEqualTo

/**
 * JUnit Jupiter extension that provides TodoItem injection for test methods.
 * This extension creates a new TodoItem before each test and makes it available
 * for injection into test methods using the @GetTodo annotation.
 */
class GetTodoExtension :
    ParameterResolver,
    BeforeEachCallback {
    private val CREATED_TODO = "CREATED_TODO"

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        val isTodoItem = parameterContext.parameter.type == TodoItem::class.java
        val isAnnotatedWithGetTodo = parameterContext.isAnnotated(GetTodo::class.java)
        return isTodoItem && isAnnotatedWithGetTodo
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): TodoItem =
        getStore(extensionContext).get(CREATED_TODO) as TodoItem

    override fun beforeEach(context: ExtensionContext) {
        getStore(context).put(
            CREATED_TODO,
            TodoItem.defaultTodoItem().apply {
                todoClient.createTodo(this).apply {
                    expectThat(this.status).isEqualTo(HttpStatusCode.Created)
                }
            },
        )
    }

    private fun getStore(context: ExtensionContext): ExtensionContext.Store = context.getStore(ExtensionContext.Namespace.GLOBAL)
}
