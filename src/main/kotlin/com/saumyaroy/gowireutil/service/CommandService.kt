package com.saumyaroy.gowireutil.service

import com.goide.sdk.GoSdkUtil
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.saumyaroy.gowireutil.domain.ExecutionResult
import java.nio.charset.Charset

class CommandService {

    fun executeModuleCommands(project: Project, commands: ArrayList<String>): Collection<ExecutionResult> {
        var results = ArrayList<ExecutionResult>()
        var goPath = GoSdkUtil.getDefaultGoPath()

        if (goPath != null) {
            if (goPath.exists())
                for (module in GoSdkUtil.getGoModules(project)) {
                    val binPaths = getBinPaths(GoSdkUtil.getGoPathBins(project, module, true))
                    if (project.basePath != null) {
                        val projectPath = project.basePath
                        if (projectPath != null) {
                            results.add(executor(goPath.path, binPaths, projectPath, commands))
                        }
                    }
                }
        }
        return results
    }

    @Throws(ExecutionException::class)
    fun executor(goPath: String, mainPath: String, path: String, commands: ArrayList<String>): ExecutionResult {
        val generalCommandLine = GeneralCommandLine(commands)
        generalCommandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
        generalCommandLine.charset = Charset.forName("UTF-8")
        generalCommandLine.setWorkDirectory(path)
        val currentMainPath = generalCommandLine.parentEnvironment["PATH"]
        val updatedMainPath = "$currentMainPath:$mainPath:$goPath"
        generalCommandLine.withEnvironment("PATH", updatedMainPath)
        val output = ExecUtil.execAndGetOutput(generalCommandLine)

        return if (output.exitCode == 0) {
            ExecutionResult(output.stdout, output.exitCode)
        } else {
            ExecutionResult(output.stderr, output.exitCode)
        }
    }

    private fun getBinPaths(paths: Collection<VirtualFile>): String {
        val list = ArrayList<String>()

        for (v in paths) {
            v.canonicalPath?.let { list.add(it) }
        }

        return list.joinToString(":")
    }
}