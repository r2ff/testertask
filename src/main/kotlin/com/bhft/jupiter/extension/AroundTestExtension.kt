package com.bhft.jupiter.extension

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

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
