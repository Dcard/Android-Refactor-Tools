package com.hiking.art.programs.resource.strings

import com.hiking.art.base.Program
import com.hiking.art.extensions.formatForDisplay
import com.hiking.art.extensions.normalizeStringResourceName
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.programs.resource.ResourceRefactoringFileType
import com.hiking.art.programs.resource.ResourceType
import java.io.File

class StringRenameProgram(
    private val projectRoot: File,
    private val renameMap: Map<String, String>
) : Program(
    "String Rename"
) {
    override fun onStart() {
        val normalizedRenameMap = renameMap.mapKeys { it.key.normalizeStringResourceName() }

        fun fixFile(
            file: File,
            fileType: ResourceRefactoringFileType
        ) = fixFile(
            file = file,
            relativePath = file.relativeTo(projectRoot).path,
            fileType = fileType,
            normalizedRenameMap = normalizedRenameMap
        )

        var totalChecked = 0
        var totalUsages = 0
        val startTime = System.currentTimeMillis()
        ProjectFiles.findStringFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            fixFile(file, ResourceRefactoringFileType.Declaration())
        }
        ProjectFiles.findCodeFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            totalUsages += fixFile(file, ResourceRefactoringFileType.Code(ResourceType.StringRes))
        }
        ProjectFiles.findXmlFiles(projectRoot).also {
            totalChecked += it.size
        }.forEach { file ->
            totalUsages += fixFile(file, ResourceRefactoringFileType.Xml(ResourceType.StringRes))
        }
        val elapsedTime = System.currentTimeMillis() - startTime
        println(
            "Checked ${totalChecked.formatForDisplay()} file(s) and renamed ${totalUsages.formatForDisplay()}" +
                " usage(s) in ${(elapsedTime / 1000f).formatForDisplay()}s."
        )
    }

    private fun fixFile(
        file: File,
        relativePath: String,
        fileType: ResourceRefactoringFileType,
        normalizedRenameMap: Map<String, String>
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
            val fixedName = normalizedRenameMap[matchResult.groups[1]!!.value.normalizeStringResourceName()].also {
                if (it != null) usages++
            }
            resultBuilder
                .append(fileContent.substring(startIndex, matchResult.range.first))
                .append(fixedName?.let { fileType.makeUsage(it) } ?: matchResult.value)
            startIndex = matchResult.range.last + 1
        }
        if (usages > 0) {
            println("Writing $relativePath...")
            file.writeText(resultBuilder.toString())
        }
        return usages
    }
}