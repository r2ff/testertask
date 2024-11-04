package com.bhft.jupiter.extension

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Extension interface for JUnit Jupiter that provides callback methods for test lifecycle events.
 * Implements BeforeAllCallback to execute setup and cleanup operations for all tests in a test class.
 * This extension ensures that onStart is called only once at the beginning of all tests
 * and onEnd is called after all tests.
 */
interface AroundTestExtension : BeforeAllCallback {
    fun onStart(context: ExtensionContext) {
    }

    fun onEnd() {
    }

    override fun beforeAll(context: ExtensionContext) {
        context.root.getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent(
            this::class.java,
        ) { _ ->
            onStart(context)
            ExtensionContext.Store.CloseableResource { onEnd() }
        }
    }
}
