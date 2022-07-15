package com.saumyaroy.gowireutil.service

import com.goide.GoConstants
import com.goide.sdk.GoSdkService
import com.goide.sdk.GoSdkUtil
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.saumyaroy.gowireutil.domain.ExecutionResult
import com.saumyaroy.gowireutil.util.Helper.getBinPaths
import java.io.File
import java.nio.charset.Charset
import kotlin.io.path.Path

/**
 * Executes command on system commandline
 */
class CommandService {

    /**
     * Executes commands on project path
     */
    fun executeModuleCommands(project: Project, command: String): Collection<ExecutionResult> {
        val results = ArrayList<ExecutionResult>()

        for (module in GoSdkUtil.getGoModules(project)) {
            if (project.basePath != null) {
                val projectPath = Path(project.basePath!!).toString()
                val goPath = GoSdkUtil.retrieveGoPath(project, module)
                val goRoot = GoSdkService.getInstance(project).getSdk(module).sdkRoot?.toNioPath().toString()
                val goBinPath = getBinPaths(GoSdkUtil.getGoPathBins(project, module, false))

                results.add(executor(goRoot, goPath, goBinPath, projectPath, command))
            }
        }
        return results
    }

    /**
     * Executes terminal commands with go path in Project Module directory
     */
    @Throws(ExecutionException::class)
    private fun executor(
        goRoot: String,
        goPath: String,
        goBinPath: String,
        path: String,
        command: String
    ): ExecutionResult {
        val generalCommandLine = executionCommandLine()
        generalCommandLine.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
        generalCommandLine.charset = Charset.forName("UTF-8")
        generalCommandLine.setWorkDirectory(path)

        generalCommandLine.environment[GoConstants.GO_ROOT] = StringUtil.notNullize(goRoot)
        generalCommandLine.environment[GoConstants.GO_PATH] = StringUtil.notNullize(goPath)

        generalCommandLine.addParameter(command)

        val paths = generalCommandLine.parentEnvironment[GoConstants.PATH]
        generalCommandLine.withEnvironment(
            GoConstants.PATH,
            StringUtil.join(paths, File.pathSeparator, goBinPath, File.pathSeparator)
        )

        val output = ExecUtil.execAndGetOutput(generalCommandLine)

        return if (output.exitCode == 0) {
            ExecutionResult(output.stdout, output.exitCode)
        } else {
            ExecutionResult(output.stderr, output.exitCode)
        }
    }


    /**
     * Setups [GeneralCommandLine] to execute on system terminal based on OS
     */
    private fun executionCommandLine(): GeneralCommandLine {
        val generalCommandLine = GeneralCommandLine()

        if (SystemInfo.isWindows) {
            generalCommandLine.exePath = ExecUtil.windowsShellName
            generalCommandLine.addParameters("/c")
        } else if (SystemInfo.isMac) {
            generalCommandLine.exePath = ExecUtil.openCommandPath
            generalCommandLine.addParameters("-a")
        } else {
            generalCommandLine.exePath = "/bin/sh"
            generalCommandLine.addParameters("-c")
        }
        return generalCommandLine
    }


}