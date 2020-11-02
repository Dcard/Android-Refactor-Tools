package com.hiking.art.programs.guidelines

import com.hiking.art.base.Program
import com.hiking.art.extensions.formatForDisplay
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.strings.StringNameGuidelines
import com.hiking.art.modules.strings.StringResHelper
import com.hiking.art.programs.resource.ResourceType
import java.io.File

class GuidelinesFixProgram : Program(
    title = "Auto Fix String Guidelines"
) {
    private sealed class FileType {

        class Code : FileType() {
            override val usageRegex: Regex = resourceType.codeUsageRegex
            override fun makeUsage(name: String) = resourceType.makeCodeUsage(name)
        }

        class Xml : FileType() {
            override val usageRegex: Regex = resourceType.xmlUsageRegex
            override fun makeUsage(name: String) = resourceType.makeXmlUsage(name)
        }

        class Strings : FileType() {
            override val usageRegex: Regex = "name=\"([a-z0-9._]+)\"".toRegex()
            override fun makeUsage(name: String) = "name=\"$name\""
        }

        abstract val usageRegex: Regex
        abstract fun makeUsage(name: String): String
    }

    companion object {
        private val resourceType = ResourceType.StringRes
    }

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
        val allStrings = StringResHelper.readStringsFromFiles(stringFiles)
        val illegalStrings = allStrings.filter { !StringNameGuidelines.isLegal(it.key) }
        if (illegalStrings.isEmpty()) {
            println("No illegal strings found.")
            return
        }
        val nameFixMap = illegalStrings.keys.mapNotNull { name ->
            StringNameGuidelines.autoFix(name)?.let { name to it }
        }.toMap()

        println("Found ${illegalStrings.size} illegal string(s), with ${nameFixMap.size} fixable name(s):")
        nameFixMap.forEach { (name, fixedName) ->
            println("- $name -> $fixedName / ${allStrings.getValue(name).joinToString(", ")}")
        }

        if (nameFixMap.isNotEmpty()) {
            println()
            println("Apply ${nameFixMap.size} fix(es)?")
            if (promptBooleanInput()) {
                fixStrings(projectRoot, nameFixMap)
            }
        }
    }

    private fun fixStrings(
        projectRoot: File,
        nameFixMap: Map<String, String>
    ) {
        fun fixFile(
            file: File,
            fileType: FileType
        ) = fixFile(
            file = file,
            relativePath = file.relativeTo(projectRoot).path,
            fileType = fileType,
            nameFixMap = nameFixMap
        )

        var totalChecked = 0
        var totalUsages = 0
        val startTime = System.currentTimeMillis()
        ProjectFiles.findStringFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            fixFile(file, FileType.Strings())
        }
        ProjectFiles.findCodeFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            totalUsages += fixFile(file, FileType.Code())
        }
        ProjectFiles.findXmlFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            totalUsages += fixFile(file, FileType.Xml())
        }
        val elapsedTime = System.currentTimeMillis() - startTime
        println(
            "Checked ${totalChecked.formatForDisplay()} file(s) and fixed ${totalUsages.formatForDisplay()} usage(s)" +
                " in ${(elapsedTime / 1000f).formatForDisplay()}s."
        )
    }

    private fun fixFile(
        file: File,
        relativePath: String,
        fileType: FileType,
        nameFixMap: Map<String, String>
    ): Int {
        println("Checking $relativePath...")
        val fileContent = file.readText()

        val resultBuilder = StringBuilder()
        var startIndex = 0
        var usages = 0
        while (true) {
            val matchResult = fileType.usageRegex.find(fileContent, startIndex)
            if (matchResult == null) {
                if (resultBuilder.isNotEmpty()) {
                    resultBuilder.append(fileContent.substring(startIndex))
                }
                break
            }
            val fixedName = nameFixMap[matchResult.groups[1]!!.value].also {
                if (it != null) usages++
            }
            resultBuilder
                .append(fileContent.substring(startIndex, matchResult.range.first))
                .append(fixedName?.let { fileType.makeUsage(it) } ?: matchResult.value)
            startIndex = matchResult.range.last + 1
        }
        if (usages > 0) {
            println("Applying fix to $relativePath...")
            file.writeText(resultBuilder.toString())
        }
        return usages
    }
}