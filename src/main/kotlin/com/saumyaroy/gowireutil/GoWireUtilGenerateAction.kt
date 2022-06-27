package com.saumyaroy.gowireutil

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.saumyaroy.gowireutil.service.FileService
import com.saumyaroy.gowireutil.service.WireService

class GoWireUtilGenerateAction : AnAction() {

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = true
    }

    override fun actionPerformed(event: AnActionEvent) {
        var fileService = FileService(event)
        fileService.saveAndRefreshFileChanges()
        wireGenerate(event, fileService)
    }

    private fun wireGenerate(event: AnActionEvent, fileService: FileService) {
        ApplicationManager.getApplication().executeOnPooledThread {
            var project = event.project
            if (project != null) {
                val wireService = WireService(project)
                if (wireService.wireExits()) {
                    wireService.wireGenerator()
                    fileService.refreshProjectFileChanges()
                }
            }
        }
    }
}

