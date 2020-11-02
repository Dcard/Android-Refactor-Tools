package com.hiking.art.modules.strings

import com.hiking.art.extensions.asCollection
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object StringFileHelper {

    fun readStringsFromFiles(
        stringFiles: List<File>
    ): Map<String, List<String>> {
        val allStrings = mutableMapOf<String, List<String>>()
        stringFiles.forEach { stringFile ->
            val stringsFromFile = readStringsFromFile(stringFile)
            stringsFromFile.forEach { (key, value) ->
                val existingValues = allStrings[key]
                allStrings += key to (existingValues?.let { existingValues + value } ?: listOf(value))
            }
        }
        return allStrings
    }

    private fun readStringsFromFile(
        stringFile: File
    ): Map<String, String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringFile)
        return doc.getElementsByTagName("string").asCollection().map { node ->
            val stringName = node.attributes.getNamedItem("name").textContent
            val stringValue = node.textContent
            stringName to stringValue
        }.toMap()
    }
}