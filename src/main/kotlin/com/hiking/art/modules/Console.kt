package com.hiking.art.modules

fun printlnErr(message: Any?) {
    System.err.println(message)
}

fun promptInput(
    validation: ((String?) -> Boolean)? = null
): String? {
    while (true) {
        print("> ")
        val input = readLine()
        if (validation?.invoke(input) != false) {
            return input
        } else {
            println("Invalid input.")
        }
    }
}

fun promptIntInput(range: IntRange) = promptIntInput { range.contains(it) }

fun promptIntInput(
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

fun promptBooleanInput(): Boolean {
    while (true) {
        print("(y/n)> ")
        when (readLine()) {
            "Y", "y", "true", "1" -> return true
            "N", "n", "false", "0" -> return false
            else -> println("Invalid input.")
        }
    }
}