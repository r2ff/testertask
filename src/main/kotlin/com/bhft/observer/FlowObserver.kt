package com.bhft.observer

import com.bhft.extension.checkTestCaseOrStep
import com.bhft.utils.AllureHelper
import com.bhft.utils.withDelay
import io.qameta.allure.Allure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FlowObserver<T>(
    private val flow: Flow<T>,
    private val step: String = "",
) {
    private val messages: MutableList<T> = mutableListOf()
    private var errorMessage: String = ""
    private val lifecycle = Allure.getLifecycle()
    private val attachment =
        lifecycle.let { lc ->
            lc.checkTestCaseOrStep {
                lc.prepareAttachment("Stream ${step}Response", "text/plain", "txt")
            } ?: ""
        }

    val attach = { data: String ->
        AllureHelper.updateAttachment(attachment, data)
    }

    init {
        observeFlow()
    }

    private fun handleException() {
        if (errorMessage.isNotBlank()) {
            val message = errorMessage
            errorMessage = ""
            throw AssertionError(message)
        }
    }

    private fun observeFlow() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                var count = 1
                flow
                    .onEach {
                        attach("[$count] [${System.currentTimeMillis()}]\n $it\n")
                        count++
                    }.collect { message ->
                        synchronized(messages) {
                            messages.add(message)
                        }
                    }
            } catch (t: Throwable) {
                errorMessage = t.localizedMessage
            }
        }
    }

    /**
     * findNext
     * возвращает имеющиеся на данный момент сообщения, соответствующие условию в block
     * @param timeout - время на ожидание и поиск значений
     * @param timeoutMessage - если заполнено, при отсутствии ответа/подходящих значений выведет ошибку с этим текстом
     * @param block - условие фильтрации возвращающее boolean
     * @receiver
     * @return
     */
    fun findNext(
        timeout: Long = 10000L,
        timeoutMessage: String = "",
        block: (T) -> Boolean = { true },
    ): List<T> {
        val startTime = System.currentTimeMillis()
        var messagesFound = synchronized(messages) { messages.toList().filter { block(it) } }
        handleException()
        messages.clear()
        while (messagesFound.isEmpty() && System.currentTimeMillis() - startTime < timeout) {
            messagesFound = withDelay(300) { synchronized(messages) { messages.toList().filter { block(it) } } }
            handleException()
            messages.clear()
        }
        if (messagesFound.isEmpty() && timeoutMessage.isNotEmpty()) {
            throw AssertionError(timeoutMessage)
        }

        return messagesFound
    }
}