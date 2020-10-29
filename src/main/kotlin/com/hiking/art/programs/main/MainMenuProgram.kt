package com.hiking.art.programs.main

import com.hiking.art.base.Program
import com.hiking.art.modules.AppFiles
import com.hiking.art.modules.promptInput
import com.hiking.art.programs.guidelines.GuidelinesCheckProgram
import com.hiking.art.programs.shared.OpenAppFileInDesktopProgram
import kotlin.system.exitProcess

class MainMenuProgram : Program(
    title = "Main Menu"
) {
    companion object {
        private const val QUIT_OPTION = "Q"
    }

    private val programs = listOf(
        GuidelinesCheckProgram(),
        OpenAppFileInDesktopProgram(AppFiles.refactorRulesFile),
    )

    override fun onStart() {
        println("Select a function: ")
        programs.forEachIndexed { index, option ->
            println("${index + 1}) ${option.optionName}")
        }
        println("$QUIT_OPTION) Quit program")
        val input = promptInput { input ->
            when {
                input == null -> false
                input.toUpperCase() == QUIT_OPTION -> true
                else -> input.toIntOrNull().let { it != null && (1..programs.size).contains(it) }
            }
        }!!
        if (input.toUpperCase() == QUIT_OPTION) {
            println("Good bye!")
            exitProcess(0)
        } else {
            programs[input.toInt() - 1].start()
        }
    }
}