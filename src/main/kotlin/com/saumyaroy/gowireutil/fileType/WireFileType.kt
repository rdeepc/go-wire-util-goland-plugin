package com.saumyaroy.gowireutil.fileType

import com.goide.GoLanguage
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.saumyaroy.gowireutil.icons.GoWireUtilIcons
import javax.swing.Icon

/**
 * Registers Wire File type for wire go files in resources/META-INF/plugin.xml
 */
class WireFileType : LanguageFileType(GoLanguage.INSTANCE), FileType {
    private val fileType = "Wire File Type"
    private val fileTypeDescription = "Go Lang wire file"
    private val fileTypeExtension = "go"

    companion object {
        // Referred by plugin.xml
        @JvmStatic
        val INSTANCE: WireFileType = WireFileType()
    }

    override fun getName(): String {
        return fileType
    }

    override fun getDescription(): String {
        return fileTypeDescription
    }

    override fun getDefaultExtension(): String {
        return fileTypeExtension
    }

    override fun getIcon(): Icon {
        return GoWireUtilIcons.GoWireMainLogo
    }
}