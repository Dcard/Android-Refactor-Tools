package com.hiking.art.programs.guidelines

import com.hiking.art.base.Program
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.strings.StringNameGuidelines
import com.hiking.art.modules.strings.StringResHelper
import com.hiking.art.modules.strings.model.StringRefactorRule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

class GuidelinesCheckProgram : Program(
    title = "Check String Guidelines"
) {
    override fun onStart() {
        val projectRoot = AppFiles.requestProjectRoot() ?: return
        readStrings(projectRoot)
    }

    private fun readStrings(projectRoot: File) {
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

        println("Found ${illegalStrings.size} illegal string(s):")
        illegalStrings.forEach { (name, value) ->
            println("- $name / ${value.joinToString(", ")}")
        }
        println()

        val refactorRulesFile = AppFiles.refactorRulesFile
        val actionName = if (refactorRulesFile.exists()) "Overwrite" else "Write"
        println("$actionName ${illegalStrings.size} result(s) into ${refactorRulesFile.name}?")
        if (promptBooleanInput()) {
            val refactorRules = illegalStrings.map {
                StringRefactorRule(
                    fromName = it.key,
                    toName = it.key,
                    values = it.value
                )
            }
            Json { prettyPrint = true }.encodeToString(
                ListSerializer(StringRefactorRule.serializer()), refactorRules
            ).let {
                refactorRulesFile.writeText(it)
            }
            println("Results are saved into ${refactorRulesFile.name}.")
        }
    }
}