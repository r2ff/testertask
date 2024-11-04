package com.bhft.config

/**
 * Configuration interface that provides access to test environment settings.
 * Supports different environments (local, remote) through a system property "test.env".
 * remote is not implemented.
 */
interface Config {
    companion object {
        fun getConfig(): Config =
            when (System.getProperty("test.env")) {
                "remote" -> throw NotImplementedError("Remote config not implemented...")
                "local" -> LocalConfig
                else -> LocalConfig
            }
    }

    fun todoUrl(): String

    fun todoWsUrl(): String

    fun userLogin(): String

    fun userPassword(): String
}
