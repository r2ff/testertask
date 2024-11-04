package com.bhft.http

import com.bhft.extension.attach
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class DefaultHttpClient(
    baseUrl: String,
    clientAgent: String = "default-client",
) {
    private val client =
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                    callTimeout(60, TimeUnit.SECONDS)
                    connectTimeout(60, TimeUnit.SECONDS)
                }
                // addInterceptor(AllureOkHttp3())
            }
            defaultRequest {
                url(baseUrl)
                contentType(ContentType.Application.Json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 20_000
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            install(UserAgent) { agent = clientAgent }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        encodeDefaults = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }

    fun get(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
        runBlocking {
            client
                .get(url) {
                    block()
                }.attach()
        }

    fun post(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
        runBlocking {
            client
                .post(url) {
                    block()
                }.attach()
        }

    fun put(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
        runBlocking {
            client
                .put(url) {
                    block()
                }.attach()
        }

    fun delete(url: String, block: HttpRequestBuilder.() -> Unit = {}) =
        runBlocking {
            client
                .delete(url) {
                    block()
                }.attach()
        }
}
