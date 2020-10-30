package com.hiking.art.modules.strings

object StringNameGuidelines {

    private const val FORMAT_SUFFIX = "_format"

    private val legalKeywords = listOf(
        "title",
        "message",
        "action",
        "hint",
        "option",
        "status",
        "description",
        "arg",
    )

    fun isLegal(name: String) = legalKeywords.any {
        name.endsWith(it) || name.endsWith(it + FORMAT_SUFFIX)
    }
}