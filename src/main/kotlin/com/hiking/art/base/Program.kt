package com.hiking.art.base

import com.hiking.art.extension.makeDivider

abstract class Program(private val title: String) {
    fun printTitle() = println(title.makeDivider())
}