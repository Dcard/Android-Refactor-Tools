package com.hiking.art.program.main

import com.hiking.art.base.Program
import com.hiking.art.program.readstrings.ReadStrings
import kotlin.system.exitProcess

class MainMenu : Program("Main Menu") {

    private sealed class Option(val title: String) {
        object ReadStrings : Option("Read strings")
        object Exit : Option("Exit")
    }

    private val options = listOf(Option.ReadStrings, Option.Exit)

    fun start() {
        while (true) {
            printTitle()
            println("Select a function: ")
            options.forEachIndexed { index, option ->
                println("${index + 1}) ${option.title}")
            }
            val choice = getIntInput { (1..options.size).contains(it) } - 1
            when (options[choice]) {
                Option.ReadStrings -> ReadStrings().start()
                Option.Exit -> exitProcess(0)
            }
        }
    }

    private fun getIntInput(
        validation: ((Int) -> Boolean)? = null
    ): Int {
        while (true) {
            print("> ")
            val input = readLine()?.toIntOrNull()
            if (input != null && validation?.invoke(input) != false) {
                return input
            } else {
                println("Invalid input.")
            }
        }
    }
}