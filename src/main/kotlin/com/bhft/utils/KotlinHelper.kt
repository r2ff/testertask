package com.bhft.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

inline fun <T> withDelay(timeoutMillis: Long = 2000, crossinline block: suspend CoroutineScope.() -> T) =
    runBlocking {
        delay(timeoutMillis)
        block()
    }
