package com.hiking.art.programs.resource.unused

import com.hiking.art.base.Program
import com.hiking.art.extensions.normalizeStringResourceName
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.modules.strings.StringResHelper
import java.io.File

class UnusedResourcesProgram : Program(
    title = "Find Unused Resources"
) {
    sealed class ResourceType(
        val usageString: String
    ) {
        object StringRes : ResourceType("string")
        object DrawableRes : ResourceType("drawable")
    }

    override fun onStart() {
        val projectRoot = AppFiles.requestProjectRoot() ?: return
        findUnusedStrings(projectRoot)
    }

    private fun findUnusedStrings(projectRoot: File) {
        val strings = StringResHelper.readStringsFromFiles(
            stringFiles = ProjectFiles.findStringFiles(projectRoot)
        ).mapKeys { it.key.normalizeStringResourceName() }.toMutableMap()
        ProjectFiles.findCodeFiles(projectRoot).forEach { codeFile ->
            val foundUsages = findResourceUsagesInCodeFile(
                codeFile = codeFile,
                type = ResourceType.StringRes
            ).filter {
                strings.containsKey(it)
            }
            if (foundUsages.isNotEmpty()) {
                foundUsages.forEach { strings.remove(it) }
                println("Excluded ${foundUsages.size} in ${codeFile.name}, ${strings.size} remains.")
            }
        }
        ProjectFiles.findXmlFiles(projectRoot).forEach { resFile ->
            val foundUsages = findResourceUsagesInXmlFile(
                xmlFile = resFile,
                type = ResourceType.StringRes
            ).map {
                it.normalizeStringResourceName()
            }.filter {
                strings.containsKey(it)
            }
            if (foundUsages.isNotEmpty()) {
                foundUsages.forEach { strings.remove(it) }
                println("Excluded ${foundUsages.size} in ${resFile.name}, ${strings.size} remains.")
            }
        }
        println()

        if (strings.isEmpty()) {
            println("No unused strings found:")
        } else {
            println("${strings.size} unused string(s) found:")
            strings.forEach { (key, value) ->
                println("- $key / ${value.joinToString(", ")}")
            }
        }
    }

    private fun findResourceUsagesInCodeFile(
        codeFile: File,
        type: ResourceType
    ): List<String> = "R\\s*\\.\\s*${type.usageString}\\s*\\.\\s*([a-z0-9_]+)".toRegex()
        .findAll(codeFile.readText()).map { it.groups[1]!!.value }.toList()

    private fun findResourceUsagesInXmlFile(
        xmlFile: File,
        type: ResourceType
    ): List<String> = "@${type.usageString}/([a-z0-9_.]+)".toRegex()
        .findAll(xmlFile.readText()).map { it.groups[1]!!.value }.toList()
}