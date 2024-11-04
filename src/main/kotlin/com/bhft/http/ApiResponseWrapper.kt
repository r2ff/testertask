package com.bhft.http

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

data class ApiResponseWrapper<T>(
    val status: HttpStatusCode,
    val body: T? = null,
    val error: String? = null,
    val rawResponse: HttpResponse,
)

suspend inline fun <reified T> HttpResponse.wrapResponse(): ApiResponseWrapper<T> =
    if (this.status.isSuccess()) {
        ApiResponseWrapper(
            status = this.status,
            body = this.body<T>(),
            rawResponse = this,
        )
    } else {
        ApiResponseWrapper(
            status = this.status,
            error = this.body<String>(),
            rawResponse = this,
        )
    }
