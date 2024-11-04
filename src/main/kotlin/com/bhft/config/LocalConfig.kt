package com.bhft.config

object LocalConfig : Config {
    override fun todoUrl() = "http://127.0.0.1:8080"

    override fun todoWsUrl() = "ws://localhost:8080"

    override fun userLogin() = "admin"

    override fun userPassword() = "admin"
}
