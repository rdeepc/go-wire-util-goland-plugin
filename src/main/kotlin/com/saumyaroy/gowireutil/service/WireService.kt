package com.saumyaroy.gowireutil.service

import com.intellij.execution.process.ProcessNotCreatedException
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.saumyaroy.gowireutil.domain.ExecutionResult

class WireService(private val project: Project) {
    private val commandService: CommandService = CommandService()
    private val notificationService: NotificationService = NotificationService()

    fun wireGenerator() {
        try {
            var commands = ArrayList<String>()
            commands.add("wire")
            commands.add("./...")
            val result = commandService.executeModuleCommands(project, commands)

            if (isFailure(result)) {
                val errorTitle = "Wire execution failed"
                notificationService.showNotification(project, errorTitle, resultsString(result), NotificationType.ERROR)
            } else {
                val successTitle = "Wire successful"
                notificationService.showNotification(
                    project,
                    successTitle,
                    resultsString(result),
                    NotificationType.INFORMATION
                )
            }
        } catch (e: ProcessNotCreatedException) {
            val errorTitle = "Wire execution failed"
            notificationService.showNotification(project, errorTitle, e.message.orEmpty(), NotificationType.ERROR)
        }
    }

    fun wireExits(): Boolean {
        val errorTitle = "Wire cant executed"
        try {
            try {
                var commands = ArrayList<String>()
                commands.add("wire")
                commands.add("help")
                commandService.executeModuleCommands(project, commands)
            } catch (e: ProcessNotCreatedException) {
                var downloadResult = wireDownload()
                if (isFailure(downloadResult)) {
                    notificationService.showNotification(project, errorTitle, resultsString(downloadResult), NotificationType.ERROR)
                }
            }
        } catch (e: ProcessNotCreatedException) {
            notificationService.showNotification(project, errorTitle, e.message.orEmpty(), NotificationType.ERROR)
            return false
        }
        return true
    }

    private fun wireDownload(): Collection<ExecutionResult> {
        var commands = ArrayList<String>()
        commands.add("go")
        commands.add("install")
        commands.add("github.com/google/wire/cmd/wire@latest")
        return commandService.executeModuleCommands(project, commands)
    }

    private fun isFailure(executionResults: Collection<ExecutionResult>): Boolean {
        val result = false

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
                result +=" \n "
            }
            result += it.result
        }

        return result
    }
}