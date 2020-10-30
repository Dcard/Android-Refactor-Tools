package com.hiking.art.base

import com.hiking.art.extensions.makeDivider

abstract class Program(
    val title: String
) {
    companion object {
        private var firstTitle = true
    }

    fun start() {
        if (firstTitle) {
            firstTitle = false
        } else {
            println()
        }
        printTitle()
        onStart()
    }

    abstract fun onStart()

    private fun printTitle() = println(title.makeDivider())
}