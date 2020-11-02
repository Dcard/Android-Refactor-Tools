package com.hiking.art.programs.main

import com.hiking.art.base.Program
import com.hiking.art.modules.files.AppFiles
import com.hiking.art.modules.promptInput
import com.hiking.art.programs.guidelines.GuidelinesCheckProgram
import com.hiking.art.programs.guidelines.GuidelinesFixProgram
import com.hiking.art.programs.resource.unused.UnusedResourcesProgram
import com.hiking.art.programs.shared.OpenAppFileInDesktopProgram
import kotlin.system.exitProcess

class MainMenuProgram : Program(
    title = "Main Menu"
) {
    private data class Option(
        val name: String,
        val action: () -> Unit
    )

    private val options = mapOf(
        "1" to Option("Find unused strings.") {
            UnusedResourcesProgram().start()
        },
        "2" to Option("Check string name guidelines.") {
            GuidelinesCheckProgram().start()
        },
        "3" to Option("Apply string name auto-fix.") {
            GuidelinesFixProgram().start()
        },
        "4" to AppFiles.refactorRulesFile.let { file ->
            Option("Open ${file.name} in desktop.") {
                OpenAppFileInDesktopProgram(file).start()
            }
        },
        "Q" to Option("Quit program.") {
            println("Good bye!")
            exitProcess(0)
        }
    )

    override fun onStart() {
        println("Select a function: ")
        options.forEach { (key, value) ->
            println("$key) ${value.name}")
        }
        while (true) {
            val input = promptInput()
            val option = input?.toUpperCase()?.let { options[it] }
            if (option != null) {
                option.action()
                break
            }
            println("Invalid input.")
        }
    }
}