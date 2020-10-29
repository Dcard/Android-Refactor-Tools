package com.hiking.art.modules

object StringNameGuidelines {

    private val legalKeywords = listOf(
        "title",
        "title_format",
        "message",
        "message_format",
        "action",
        "action_format",
        "hint",
        "hint_format",
        "option",
        "option_format",
        "status",
        "status_format",
        "description",
        "description_format",
        "arg",
        "arg_format",
        "display_format"
    )

    fun isLegal(name: String) = legalKeywords.any { name.endsWith(it) }
}