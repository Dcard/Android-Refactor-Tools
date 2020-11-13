package com.hiking.art.modules.strings

object StringNameGuidelinesHelper {

    private const val FORMAT_SUFFIX = "format"
    private const val SEPARATOR = '.'
    private const val FIXABLE_SEPARATOR = '_'

    private val legalKeywords = listOf(
        "title",
        "message",
        "action",
        "hint",
        "option",
        "status",
        "description",
        "arg",
        "pattern"
    )

    fun isLegal(
        name: String,
        values: List<String>
    ) = isSeparatorCountLegal(name) && isKeywordLegal(name) && isSuffixLegal(name, values)

    fun autoFix(name: String): String? {
        val fixableSuffix = run {
            legalKeywords.map { FIXABLE_SEPARATOR + it }.forEach {
                if (name.endsWith(it)) return@run it
                else if (name.endsWith("${it}_$FORMAT_SUFFIX")) return@run it + FORMAT_SUFFIX
            }
            if (name.count { it == SEPARATOR } == 1) {
                legalKeywords.map { SEPARATOR + it }.forEach {
                    if (name.endsWith(it)) return@run it
                    else if (name.endsWith("${it}_$FORMAT_SUFFIX")) return@run it + FORMAT_SUFFIX
                }
            }
            null
        } ?: return null

        val suffixRemovedName = name.removeSuffix(fixableSuffix)
        val insertion = if (suffixRemovedName.count { it == SEPARATOR } == 0) {
            SEPARATOR + "main"
        } else ""
        return suffixRemovedName + insertion + fixableSuffix.replaceFirst(FIXABLE_SEPARATOR, SEPARATOR)
    }

    private fun isSeparatorCountLegal(name: String) = name.count { it == SEPARATOR } == 2

    private fun isKeywordLegal(
        name: String
    ): Boolean = legalKeywords.map { SEPARATOR + it }.any {
        name.endsWith(it) || name.endsWith("${it}_$FORMAT_SUFFIX")
    }

    private fun isSuffixLegal(
        name: String,
        values: List<String>
    ): Boolean = name.endsWith(FORMAT_SUFFIX) == listOf(
        "%(?:\\d+\\$)?s".toRegex(),
        "%(?:\\d+\\$)?f".toRegex(),
        "%(?:\\d+\\$)?d".toRegex()
    ).any { regex -> values.all { regex.containsMatchIn(it) } }
}