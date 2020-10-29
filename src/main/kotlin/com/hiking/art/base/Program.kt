package com.hiking.art.base

import com.hiking.art.extensions.makeDivider

abstract class Program(private val title: String) {
    fun printTitle() = println(title.makeDivider())
}