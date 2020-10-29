package com.hiking.art.modules

fun printlnErr(message: Any?) {
    System.err.println(message)
}

fun readInt(range: IntRange) = readInt { range.contains(it) }

fun readInt(
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