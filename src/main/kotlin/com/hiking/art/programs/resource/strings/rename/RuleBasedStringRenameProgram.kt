package com.hiking.art.programs.resource.strings.rename

import com.hiking.art.base.Program
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.printlnErr
import com.hiking.art.modules.promptBooleanInput
import com.hiking.art.modules.strings.model.StringRefactorRule
import com.hiking.art.programs.resource.strings.StringRenameProgram
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File

class RuleBasedStringRenameProgram(
    private val rulesFile: File
) : Program(
    title = "Rule-Based String Rename"
) {
    override fun onStart() {
        val projectRoot = AppFiles.requestProjectRoot() ?: return
        checkToRenameStrings(projectRoot)
    }

    private fun checkToRenameStrings(projectRoot: File) {
        println("Reading rules from ${rulesFile.path}...")
        val rules = try {
            Json.decodeFromString(
                MapSerializer(String.serializer(), StringRefactorRule.serializer()),
                rulesFile.readText()
            )
        } catch (e: Exception) {
            printlnErr("Failed to read ${rulesFile.name}: ${e.javaClass.simpleName} ${e.message}")
            return
        }.filter {
            it.key != it.value.toName
        }
        if (rules.isEmpty()) {
            println("No rules to apply. Make sure you've make changes to ${rulesFile.name}.")
            return
        }

        rules.forEach { (name, rule) ->
            println("- $name -> ${rule.toName}}")
        }
        println()
        println("Apply ${rules.size} rule(s)?")
        if (promptBooleanInput()) {
            StringRenameProgram(
                projectRoot = projectRoot,
                renameMap = rules.map { it.key to it.value.toName }.toMap()
            ).start()
        }
    }
}