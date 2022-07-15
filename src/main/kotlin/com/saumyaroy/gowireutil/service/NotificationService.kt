package com.saumyaroy.gowireutil.service

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Shows notification on Intellij UI
 */
class NotificationService {
    private val notificationGroupId = "Go Wire Util"

    fun showNotification(project: Project, title: String, message: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(notificationGroupId)
            .createNotification(title, message, type)
            .notify(project)
    }
}