package com.hiking.art.programs.guidelines

import com.hiking.art.base.Program
import com.hiking.art.extensions.asCollection
import com.hiking.art.modules.AppFiles
import com.hiking.art.modules.StringNameGuidelines
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.refactor.StringRefactorRule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class GuidelinesCheckProgram : Program(
    title = "Check String Guidelines",
    optionName = "Check string guidelines."
) {
    override fun onStart() {
        val projectRoot = AppFiles.requestProjectRoot() ?: return
        readStrings(projectRoot)
    }

    private fun readStrings(projectRoot: File) {
        val moduleSrcFolders = projectRoot.listFiles { file -> file.isDirectory }!!.mapNotNull { dir ->
            File(dir, "src").let { if (it.isDirectory) it else null }
        }
        val resFolders = moduleSrcFolders.flatMap { srcFolder ->
            srcFolder.listFiles { file -> file.isDirectory }!!.mapNotNull { buildFolder ->
                if (buildFolder.name == "test") null else {
                    File(buildFolder, "res").let { if (it.isDirectory) it else null }
                }
            }
        }
        val stringFiles = resFolders.flatMap { resFolder ->
            resFolder.listFiles { file ->
                file.isDirectory && file.name.startsWith("values")
            }!!.mapNotNull { valuesFolder ->
                File(valuesFolder, "strings.xml").let { if (it.isFile) it else null }
            }
        }
        if (stringFiles.isEmpty()) {
            println("Not string files found.")
            return
        }

        val refactorRules = mutableMapOf<String, StringRefactorRule>()
        stringFiles.forEach { stringFile ->
            val relativePath = stringFile.relativeTo(projectRoot).path
            println("Checking $relativePath...")
            val illegalStrings = findIllegalStrings(stringFile)
            illegalStrings.forEach { println("${it.key} / ${it.value}") }
            println("${illegalStrings.size} illegal strings in $relativePath.")
            println()

            illegalStrings.forEach {
                val existingRule = refactorRules[it.key]
                refactorRules += it.key to (existingRule?.copy(
                    values = existingRule.values + it.value
                ) ?: StringRefactorRule(
                    fromName = it.key,
                    toName = it.key,
                    values = listOf(it.value)
                ))
            }
        }
        val refactorRulesFile = AppFiles.refactorRulesFile
        val actionName = if (refactorRulesFile.exists()) "Overwrite" else "Write"
        println("$actionName ${refactorRules.size} results into ${refactorRulesFile.name}?")
        if (promptBooleanInput()) {
            Json { prettyPrint = true }.encodeToString(
                ListSerializer(StringRefactorRule.serializer()), refactorRules.values.toList()
            ).let {
                refactorRulesFile.writeText(it)
            }
            println("Results are saved into ${refactorRulesFile.name}.")
        }
    }

    private fun findIllegalStrings(stringFile: File): Map<String, String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringFile)
        val allStrings = doc.getElementsByTagName("string").asCollection().map { node ->
            val stringName = node.attributes.getNamedItem("name").textContent
            val stringValue = node.textContent
            stringName to stringValue
        }.toMap()
        return allStrings.filter { !StringNameGuidelines.isLegal(it.key) }
    }
}