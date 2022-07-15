package com.saumyaroy.gowireutil

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.saumyaroy.gowireutil.service.FileService
import com.saumyaroy.gowireutil.service.GoService
import com.saumyaroy.gowireutil.service.NotificationService
import com.saumyaroy.gowireutil.service.WireService

class GoWireUtilGenerateAction : AnAction() {
    private val notificationService: NotificationService = NotificationService()

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = true
    }

    override fun actionPerformed(event: AnActionEvent) {
        val fileService = FileService(event)
        fileService.saveAndRefreshFileChanges()
        wireGenerate(event, fileService)
    }

    private fun wireGenerate(event: AnActionEvent, fileService: FileService) {
        ApplicationManager.getApplication().executeOnPooledThread {
            val project = event.project
            if (project != null) {
                val goService = GoService(project, notificationService)
                if (goService.isGoSDKReady()) {
                    val wireService = WireService(project, notificationService)
                    if (wireService.wireExits()) {
                        wireService.wireGenerator()
                        fileService.refreshProjectFileChanges()
                    }
                }
            }
        }
    }
}

