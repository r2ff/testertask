package com.bhft.extension

import com.bhft.utils.AllureHelper.updateAttachment
import io.qameta.allure.AllureLifecycle

fun <T : Any> AllureLifecycle.checkTestCaseOrStep(block: (lifecycle: AllureLifecycle) -> T): T? {
    val caseOrStep = currentTestCaseOrStep
    return if (caseOrStep.isPresent) {
        block(this)
    } else {
        null
    }
}

fun AllureLifecycle.addAttachmentInCurrentTestOrStep(name: String, content: String): String? =
    checkTestCaseOrStep {
        prepareAttachment(name, "text/plain", "txt").apply {
            updateAttachment(this, content)
        }
    }
