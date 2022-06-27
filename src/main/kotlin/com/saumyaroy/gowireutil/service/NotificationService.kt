package com.saumyaroy.gowireutil.service

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class NotificationService {

    fun showNotification(project: Project, title: String, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Go Wire Util")
            .createNotification(title, message, type)
            .notify(project)
    }
}