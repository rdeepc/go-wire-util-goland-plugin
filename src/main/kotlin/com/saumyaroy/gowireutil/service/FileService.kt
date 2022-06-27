package com.saumyaroy.gowireutil.service

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

class FileService(private val event: AnActionEvent) {

    fun saveAndRefreshFileChanges() {
        WriteAction.run<Exception> {
            FileDocumentManager.getInstance().saveAllDocuments()
        }
    }

    fun refreshProjectFileChanges() {
        val currentFilePath: VirtualFile? = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        VfsUtil.markDirtyAndRefresh(false, true, true, currentFilePath)
    }
}