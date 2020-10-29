package com.hiking.art.programs.guidelines

import com.hiking.art.base.Program
import com.hiking.art.extensions.asCollection
import com.hiking.art.modules.AppFiles
import com.hiking.art.modules.StringNameGuidelines
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.promptIntInput
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
            println("Not string file is found.")
        } else {
            println("Select a file to check:")
            println("0) Go back.")
            stringFiles.forEachIndexed { index, file ->
                println("${index + 1}) ${file.relativeTo(projectRoot).path}")
            }
            val input = promptIntInput(0..stringFiles.size)
            if (input != 0) {
                val stringFile = stringFiles[input - 1]
                println("Checking ${stringFile.relativeTo(projectRoot).path}...")
                readStringFile(stringFile)
            }
        }
    }

    private fun readStringFile(stringFile: File) {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringFile)
        val allStrings = doc.getElementsByTagName("string").asCollection().map { node ->
            val stringName = node.attributes.getNamedItem("name").textContent
            val stringValue = node.textContent
            stringName to stringValue
        }.toMap()

        val illegalStrings = allStrings.filter { !StringNameGuidelines.isLegal(it.key) }
        illegalStrings.forEach { println("${it.key} / ${it.value}") }
        println("${illegalStrings.size} of ${allStrings.size} is illegal in file.")

        println()
        val refactorRulesFile = AppFiles.refactorRulesFile
        val actionName = if (refactorRulesFile.exists()) "Overwrite" else "Write"
        println("$actionName results into ${refactorRulesFile.name}?")
        if (promptBooleanInput()) {
            val rules = illegalStrings.map {
                StringRefactorRule(
                    fromName = it.key,
                    toName = it.key,
                    description = it.value
                )
            }
            Json { prettyPrint = true }.encodeToString(
                ListSerializer(StringRefactorRule.serializer()), rules
            ).let {
                refactorRulesFile.writeText(it)
            }
            println("Results are saved into ${refactorRulesFile.name}.")
        }
    }
}