package com.saumyaroy.gowireutil.service

import com.goide.sdk.GoSdkService
import com.goide.sdk.GoSdkUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.saumyaroy.gowireutil.util.Helper.getBinPaths
import com.saumyaroy.gowireutil.util.NotificationText

class GoService(private val project: Project, private val notificationService: NotificationService) {
    private val projectEmptyMessage = "Project base path empty"
    private val goPathEmptyMessage = "Go path empty"
    private val goRootEmptyMessage = "Go root empty"
    private val goBinEmptyMessage = "Go bin empty"
    private val goModuleEmptyMessage = "Go module empty"
    private val checkGoSetupMessage = "Check Go setup"

    fun isGoSDKReady(): Boolean {
        val modules = GoSdkUtil.getGoModules(project)

        if (modules.isEmpty()) {
            failureNotification(goModuleEmptyMessage)
            return false
        }

        for (module in modules) {
            if (project.basePath != null && project.basePath!!.isNotEmpty()) {
                val goPath = GoSdkUtil.retrieveGoPath(project, module)
                if (goPath.isEmpty()) {
                    failureNotification(goPathEmptyMessage)
                    return false
                }

                val goRoot = GoSdkService.getInstance(project).getSdk(module).sdkRoot?.toNioPath().toString()
                if (goRoot.isEmpty()) {
                    failureNotification(goRootEmptyMessage)
                    return false
                }

                val goBinPath = getBinPaths(GoSdkUtil.getGoPathBins(project, module, false))
                if (goBinPath.isEmpty()) {
                    failureNotification(goBinEmptyMessage)
                    return false
                }

            } else {
                failureNotification(projectEmptyMessage)
                return false
            }
        }
        return true
    }

    private fun failureNotification(message: String) {
        notificationService.showNotification(
            project, NotificationText.WIRE_FAILURE_TITLE,
            "$message,$checkGoSetupMessage", NotificationType.ERROR
        )
    }
}