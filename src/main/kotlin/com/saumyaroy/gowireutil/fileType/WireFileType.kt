package com.saumyaroy.gowireutil.fileType

import com.goide.GoLanguage
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.saumyaroy.gowireutil.icons.GoWireUtilIcons
import javax.swing.Icon

class WireFileType : LanguageFileType(GoLanguage.INSTANCE), FileType {

    companion object {
        @JvmStatic
        val INSTANCE: WireFileType = WireFileType()
    }

    override fun getName(): String {
        return "Wire File Type"
    }

    override fun getDescription(): String {
        return "Go Lang wire file"
    }

    override fun getDefaultExtension(): String {
        return "go"
    }

    override fun getIcon(): Icon? {
        return GoWireUtilIcons.GoWireMainLogo
    }
}