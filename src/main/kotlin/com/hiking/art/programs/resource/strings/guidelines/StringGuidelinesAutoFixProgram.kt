package com.hiking.art.programs.resource.strings.guidelines

import com.hiking.art.base.Program
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.strings.StringFileHelper
import com.hiking.art.modules.strings.StringNameGuidelinesHelper
import com.hiking.art.programs.resource.strings.StringRenameProgram
import java.io.File

class StringGuidelinesAutoFixProgram : Program(
    title = "String Guidelines Auto-Fix"
) {
    override fun onStart() {
        val projectRoot = AppFiles.requestProjectRoot() ?: return
        checkToFixStrings(projectRoot)
    }

    private fun checkToFixStrings(projectRoot: File) {
        val stringFiles = ProjectFiles.findStringFiles(projectRoot)
        if (stringFiles.isEmpty()) {
            println("No string files found.")
            return
        }
        val allStrings = StringFileHelper.readStringsFromFiles(stringFiles)
        val illegalStrings = allStrings.filter { !StringNameGuidelinesHelper.isLegal(it.key) }
        if (illegalStrings.isEmpty()) {
            println("No illegal strings found.")
            return
        }
        val renameMap = illegalStrings.keys.mapNotNull { name ->
            StringNameGuidelinesHelper.autoFix(name)?.let { name to it }
        }.toMap()

        println("Found ${illegalStrings.size} illegal string(s), with ${renameMap.size} fixable name(s):")
        renameMap.forEach { (name, toName) ->
            println("- $name -> $toName / ${allStrings.getValue(name).joinToString(", ")}")
        }

        if (renameMap.isNotEmpty()) {
            println()
            println("Apply ${renameMap.size} fix(es)?")
            if (promptBooleanInput()) {
                StringRenameProgram(
                    projectRoot = projectRoot,
                    renameMap = renameMap
                ).start()
            }
        }
    }
}