package com.saumyaroy.gowireutil.service

import com.goide.sdk.GoSdkUtil
import com.goide.util.GoGetPackageUtil
import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.saumyaroy.gowireutil.domain.ExecutionResult
import com.saumyaroy.gowireutil.util.NotificationText.WIRE_FAILURE_TITLE
import com.saumyaroy.gowireutil.util.NotificationText.WIRE_SUCCESS_TITLE

/**
 * Executes wire library @see https://github.com/google/wire
 */
class WireService(private val project: Project, private val notificationService: NotificationService) {
    private val wireDownloadURL = "github.com/google/wire/cmd/wire@latest"
    private val wireGenerationCommand = "wire ./..."
    private val wireHelpCommand = "wire help"

    private val commandService: CommandService = CommandService()

    fun wireGenerator() {
        try {
            val result = commandService.executeModuleCommands(project, wireGenerationCommand)

            if (isFailure(result)) {
                notificationService.showNotification(
                    project,
                    WIRE_FAILURE_TITLE,
                    resultsString(result),
                    NotificationType.ERROR
                )
            } else {
                notificationService.showNotification(
                    project,
                    WIRE_SUCCESS_TITLE,
                    resultsString(result),
                    NotificationType.INFORMATION
                )
            }
        } catch (e: ProcessNotCreatedException) {
            notificationService.showNotification(
                project,
                WIRE_FAILURE_TITLE,
                e.message.orEmpty(),
                NotificationType.ERROR
            )
        }
    }

    fun wireExits(): Boolean {
        try {

            val result = commandService.executeModuleCommands(project, wireHelpCommand)
            if (isFailure(result)) {
                ApplicationManager.getApplication().invokeAndWait {
                    for (module in GoSdkUtil.getGoModules(project)) {
                        GoGetPackageUtil.installTool(
                            project,
                            module,
                            project.basePath,
                            wireDownloadURL
                        )
                    }
                }
            }
        } catch (e: ProcessNotCreatedException) {
            notificationService.showNotification(
                project,
                WIRE_FAILURE_TITLE,
                e.message.orEmpty(),
                NotificationType.ERROR
            )
            return false
        }
        return true
    }

    private fun isFailure(executionResults: Collection<ExecutionResult>): Boolean {
        val result = false

        if (executionResults.isEmpty()) {
            return true
        }

        executionResults.forEach {
            if (it.exitCode != 0) {
                return true
            }
        }
        return result
    }

    private fun resultsString(executionResults: Collection<ExecutionResult>): String {
        var result = ""

        executionResults.forEach {
            if (result.isNotEmpty()) {
                result += " \n "
            }
            result += it.result
        }

        return result
    }
}