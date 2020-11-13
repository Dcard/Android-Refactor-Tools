package com.hiking.art.programs.resource.strings.guidelines

import com.hiking.art.base.Program
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.files.ProjectFiles
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.strings.StringFileHelper
import com.hiking.art.modules.strings.StringNameGuidelinesHelper
import com.hiking.art.modules.strings.model.StringRefactorRule
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File

class StringGuidelinesCheckProgram : Program(
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
        val allStrings = StringFileHelper.readStringsFromFiles(stringFiles)
        val illegalStrings = allStrings.filter { !StringNameGuidelinesHelper.isLegal(it.key, it.value) }
        if (illegalStrings.isEmpty()) {
            println("No illegal strings found.")
            return
        }

        println("Found ${illegalStrings.size} illegal string(s):")
        illegalStrings.forEach { (name, value) ->
            println("- $name / ${value.joinToString(", ")}")
        }
        println()

        val refactorRulesFile = AppFiles.stringRenamingRulesFile
        val actionName = if (refactorRulesFile.exists()) "Overwrite" else "Write"
        println("$actionName ${illegalStrings.size} result(s) into ${refactorRulesFile.name}?")
        if (promptBooleanInput()) {
            val refactorRules = illegalStrings.map {
                it.key to StringRefactorRule(
                    toName = it.key,
                    values = it.value
                )
            }.toMap()
            Json { prettyPrint = true }.encodeToString(
                MapSerializer(String.serializer(), StringRefactorRule.serializer()), refactorRules
            ).let {
                refactorRulesFile.writeText(it)
            }
            println("Results are saved into ${refactorRulesFile.name}.")
        }
    }
}