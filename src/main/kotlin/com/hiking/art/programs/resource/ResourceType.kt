package com.hiking.art.programs.resource

import com.hiking.art.extensions.normalizeStringResourceName

sealed class ResourceType(
    private val usageString: String
) {
    object StringRes : ResourceType("string")

    val codeUsageRegex = "R\\s*\\.\\s*$usageString\\s*\\.\\s*([a-z0-9_]+)".toRegex()
    val xmlUsageRegex = "@$usageString/([a-z0-9_.]+)".toRegex()

    fun makeCodeUsage(name: String) = "R.$usageString.${name.normalizeStringResourceName()}"
    fun makeXmlUsage(name: String) = "@string/$name"
}