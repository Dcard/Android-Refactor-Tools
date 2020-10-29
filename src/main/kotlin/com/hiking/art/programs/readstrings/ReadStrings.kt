package com.hiking.art.programs.readstrings

import com.hiking.art.base.Program
import com.hiking.art.modules.configs.ConfigsHelper
import com.hiking.art.modules.printlnErr
import com.hiking.art.modules.readInt
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ReadStrings : Program("Read Strings") {

    fun start() {
        printTitle()
        val configs = try {
            ConfigsHelper.readConfigs()
        } catch (e: Exception) {
            printlnErr("Failed to read configs: ${e.javaClass.simpleName} ${e.message}")
            return
        }
        val projectRoot = File(configs.projectRoot).also {
            if (!it.exists()) printlnErr("Project root ${configs.projectRoot} does not exist!")
            if (!it.isDirectory) printlnErr("Project root ${configs.projectRoot} is not a directory!")
        }
        readStrings(projectRoot)
    }

    private fun readStrings(projectRoot: File) {
        val moduleSrcFolders = projectRoot.listFiles { file -> file.isDirectory }!!.mapNotNull { dir ->
            File(dir, "src").let { if (it.isDirectory) it else null }
        }
        val resFolders = moduleSrcFolders.flatMap { srcFolder ->
            srcFolder.listFiles { file -> file.isDirectory }!!.mapNotNull { buildFolder ->
                if (buildFolder.name == "test") null else {
                    File(buildFolder, "res").let { if (it.isDirectory) it else null }
                }
            }
        }
        val stringFiles = resFolders.flatMap { resFolder ->
            resFolder.listFiles { file ->
                file.isDirectory && file.name.startsWith("values")
            }!!.mapNotNull { valuesFolder ->
                File(valuesFolder, "strings.xml").let { if (it.isFile) it else null }
            }
        }
        if (stringFiles.isEmpty()) {
            println("Not string file is found.")
        } else {
            println("Found string files:")
            stringFiles.forEachIndexed { index, file -> println("${index + 1}) ${file.absolutePath}") }
            println("> ")
            val selection = readInt(1..stringFiles.size) - 1
            readStringFile(stringFiles[selection])
        }
    }

    private fun readStringFile(stringFile: File) {
        println("Content of ${stringFile.absolutePath}:")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringFile)
        val stringNodes = doc.getElementsByTagName("string")
        for (i in 0 until stringNodes.length) {
            val node = stringNodes.item(i)
            println("${node.attributes.getNamedItem("name").textContent} -> ${node.textContent}")
        }
        println("${stringNodes.length} string(s) in file.")
    }
}