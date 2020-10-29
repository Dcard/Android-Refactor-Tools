package com.hiking.art.programs.main

import com.hiking.art.base.Program
import com.hiking.art.modules.readInt
import com.hiking.art.programs.readstrings.ReadStrings
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
            val choice = readInt(1..options.size) - 1
            when (options[choice]) {
                Option.ReadStrings -> ReadStrings().start()
                Option.Exit -> exitProcess(0)
            }
        }
    }
}