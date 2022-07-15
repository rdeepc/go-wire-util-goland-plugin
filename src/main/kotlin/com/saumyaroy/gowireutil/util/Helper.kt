package com.saumyaroy.gowireutil.util

import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import kotlin.io.path.Path

object Helper {

    /**
     * Merges list of [VirtualFile] and formats paths strings
     */
    fun getBinPaths(paths: Collection<VirtualFile>): String {
        val list = ArrayList<String>()

        for (path in paths) {
            path.canonicalPath?.let { list.add(Path(it).toString()) }
        }

        return list.joinToString(File.pathSeparator)
    }
}