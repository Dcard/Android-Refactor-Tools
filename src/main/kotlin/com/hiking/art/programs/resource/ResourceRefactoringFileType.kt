package com.hiking.art.programs.resource

sealed class ResourceRefactoringFileType {

    class Code(private val resourceType: ResourceType) : ResourceRefactoringFileType() {
        override val usageRegex: Regex = resourceType.codeUsageRegex
        override fun makeUsage(name: String) = resourceType.makeCodeUsage(name)
    }

    class Xml(private val resourceType: ResourceType) : ResourceRefactoringFileType() {
        override val usageRegex: Regex = resourceType.xmlUsageRegex
        override fun makeUsage(name: String) = resourceType.makeXmlUsage(name)
    }

    class Declaration : ResourceRefactoringFileType() {
        override val usageRegex: Regex = "name=\"([a-z0-9._]+)\"".toRegex()
        override fun makeUsage(name: String) = "name=\"$name\""
    }

    abstract val usageRegex: Regex
    abstract fun makeUsage(name: String): String
}