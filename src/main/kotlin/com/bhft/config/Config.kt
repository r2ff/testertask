package com.bhft.config

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
