package com.bhft.extension

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.qameta.allure.Allure
import io.qameta.allure.model.Status
import io.qameta.allure.model.StepResult
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okio.Buffer
import okio.GzipSource
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

fun HttpRequestBuilder.authorization(login: String?, password: String?) =
    headers.apply {
        if (!login.isNullOrBlank() && !password.isNullOrBlank()) {
            headers.append(HttpHeaders.Authorization, Credentials.basic(login, password))
        }
    }

fun HttpResponse.attach() =
    apply {
        val request = request
        val result = StepResult()
        val uuid = UUID.randomUUID().toString()
        result.name = "[${request.method.value} ${request.url.encodedPath} ${request.url.encodedQuery}]"
        Allure.getLifecycle().apply {
            checkTestCaseOrStep {
                startStep(uuid, result)
            }
        }
        Allure.getLifecycle().addAttachmentInCurrentTestOrStep(
            "Request",
            request.getRequestParametersForLogging(true),
        )

        try {
            Allure.getLifecycle().addAttachmentInCurrentTestOrStep(
                "Response",
                getResponseParametersForLogging(true),
            )
            Allure.getLifecycle().apply {
                checkTestCaseOrStep {
                    updateStep(uuid) {
                        it.status = Status.PASSED
                    }
                }
            }
        } catch (t: Throwable) {
            Allure.getLifecycle().apply {
                checkTestCaseOrStep {
                    updateStep(uuid) {
                        it.status = Status.FAILED
                        it.description = t.message
                    }
                }
            }
            throw t
        } finally {
            Allure.getLifecycle().apply {
                checkTestCaseOrStep {
                    stopStep(uuid)
                }
            }
        }
    }

private fun HttpRequest.getRequestParametersForLogging(withHeaders: Boolean = false) =
    runBlocking {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            appendLine("Time: ${System.currentTimeMillis().toString().replace("T", " ")}")
            appendLine("${method.value}: $url")
            appendHeaders(headers, withHeaders)
            val bodyString = bodyToLoggingString()
            if (bodyString.isNotBlank()) {
                appendLine("Body:\n$bodyString")
            }
            appendLine()
        }
        return@runBlocking stringBuilder.toString()
    }

private fun HttpResponse.getResponseParametersForLogging(withHeaders: Boolean = false): String =
    runBlocking {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            try {
                val time = responseTime.timestamp - requestTime.timestamp
                appendLine("Duration: ${if (time > 999) "${time / 1000.0} s" else "$time ms"}")
            } catch (e: Exception) {
                appendLine("Duration: N/A")
            }
            appendLine("Code: ${status.value}")
            val message = status.description
            if (message.isNotBlank()) appendLine("message: $message")
            appendHeaders(headers, withHeaders)

            val bodyString = bodyToLoggingString()
            if (bodyString.isNotBlank()) {
                appendLine("Body:\n$bodyString")
            }
        }
        stringBuilder.toString()
    }

private fun StringBuilder.appendHeaders(headers: Headers, withHeaders: Boolean) {
    if (withHeaders) {
        appendLine("Headers:")
        headers.toLoggingMap().forEach {
            appendLine("    ${it.key}: ${it.value}")
        }
    }
}

private fun Headers.toLoggingMap(): Map<String, String> =
    mutableMapOf<String, String>().also {
        this.toMap().forEach { (key, value) ->
            it[key] = value.joinToString(separator = "; ")
        }
    }

private suspend fun HttpRequest.bodyToLoggingString(): String = content.asString()

@OptIn(InternalAPI::class)
private suspend fun HttpResponse.bodyToLoggingString(): String {
    val charset: Charset = contentType()?.charset() ?: StandardCharsets.UTF_8
    val source =
        try {
            bodyAsText()
        } catch (t: Throwable) {
            content.toString()
        }

    var responseBodyBuffer = Buffer().readFrom(source.byteInputStream(StandardCharsets.UTF_8))
    if ("gzip".equals(headers[HttpHeaders.ContentEncoding], ignoreCase = true)) {
        GzipSource(responseBodyBuffer.clone()).use { gzippedResponseBody ->
            responseBodyBuffer = Buffer()
            responseBodyBuffer.writeAll(gzippedResponseBody)
        }
    }
    val responseBodyString = responseBodyBuffer.clone().readString(charset)
    if (responseBodyString.contains("EmptyContent")) return responseBodyString
    return responseBodyString
}

private suspend fun OutgoingContent.asString(): String =
    when (this) {
        is OutgoingContent.ByteArrayContent -> bytes().toString(Charsets.UTF_8)
        is OutgoingContent.ReadChannelContent -> {
            val channel = readFrom()
            val byteBuffer = ByteBuffer.allocate(Int.MAX_VALUE)
            channel.readAvailable(byteBuffer)
            byteBuffer.flip()
            val byteArray = ByteArray(byteBuffer.remaining())
            byteBuffer.get(byteArray)
            String(byteArray, Charsets.UTF_8)
        }

        else -> ""
    }
