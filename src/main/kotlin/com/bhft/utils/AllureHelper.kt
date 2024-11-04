package com.bhft.utils

import io.qameta.allure.AllureResultsWriteException
import io.qameta.allure.util.PropertiesUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

object AllureHelper {
    private fun getOrCreateBasePath(): Path {
        val properties = PropertiesUtils.loadAllureProperties()
        val path = properties.getProperty("allure.results.directory", "allure-results")
        val directory = Paths.get(path)
        try {
            Files.createDirectories(directory)
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not create Allure results directory", e)
        }
        return directory
    }

    private val reportBaseDir = getOrCreateBasePath()

    fun updateAttachment(source: String, data: String) {
        val file = reportBaseDir.resolve(source)
        try {
            CoroutineScope(Dispatchers.IO).launch {
                Files.write(
                    file,
                    data.toByteArray(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND,
                )
            }
        } catch (e: IOException) {
            throw AllureResultsWriteException("Could not write Allure attachment", e)
        }
    }
}
