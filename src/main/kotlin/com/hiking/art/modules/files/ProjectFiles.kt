package com.hiking.art.modules.files

import java.io.File

object ProjectFiles {

    fun findCodeFiles(
        projectRoot: File
    ): List<File> = findCodeFolders(projectRoot).flatMap { codeFolder ->
        findCodeFilesInFolder(codeFolder)
    }

    fun findXmlFiles(
        projectRoot: File
    ): List<File> {
        val buildFolders = findBuildFolders(projectRoot)
        val manifestFiles = buildFolders.mapNotNull { buildFolder ->
            File(buildFolder, "AndroidManifest.xml").let { if (it.exists()) it else null }
        }
        val resFiles = findResFolders(
            projectRoot = projectRoot,
            inFolders = buildFolders
        ).flatMap { resFolder ->
            findResXmlFilesInFolder(resFolder)
        }
        return manifestFiles + resFiles
    }

    fun findStringFiles(
        projectRoot: File
    ): List<File> = findValuesFolders(projectRoot).mapNotNull { valuesFolder ->
        File(valuesFolder, "strings.xml").let { if (it.isFile) it else null }
    }

    private fun findSrcFolders(
        projectRoot: File
    ): List<File> = projectRoot.listFiles { file -> file.isDirectory }!!.mapNotNull { dir ->
        File(dir, "src").let { if (it.isDirectory) it else null }
    }

    private fun findCodeFolders(
        projectRoot: File
    ): List<File> = findSrcFolders(projectRoot).flatMap { srcFolder ->
        srcFolder.listFiles { file -> file.isDirectory }!!.flatMap { buildFolder ->
            if (buildFolder.name == "test") emptyList()
            else listOf("java", "kotlin").flatMap { langFolder ->
                File(buildFolder, langFolder).let {
                    if (it.isDirectory) listOf(it) else emptyList()
                }
            }
        }
    }

    private fun findCodeFilesInFolder(
        codeFolder: File
    ): List<File> = codeFolder.listFiles()!!.flatMap {
        if (it.isDirectory) {
            findCodeFilesInFolder(it)
        } else if (it.name.endsWith(".kt") || it.name.endsWith(".java")) {
            listOf(it)
        } else {
            emptyList()
        }
    }

    private fun findBuildFolders(
        projectRoot: File
    ): List<File> = findSrcFolders(projectRoot).flatMap { srcFolder ->
        srcFolder.listFiles { file -> file.isDirectory }!!.toList()
    }

    private fun findResFolders(
        projectRoot: File,
        inFolders: List<File> = findBuildFolders(projectRoot)
    ): List<File> = inFolders.mapNotNull { buildFolder ->
        if (buildFolder.name == "test") null else {
            File(buildFolder, "res").let { if (it.isDirectory) it else null }
        }
    }

    private fun findValuesFolders(
        projectRoot: File
    ): List<File> = findResFolders(projectRoot).flatMap { resFolder ->
        resFolder.listFiles { file ->
            file.isDirectory && file.name.startsWith("values")
        }!!.toList()
    }

    private fun findResXmlFilesInFolder(
        resFolder: File
    ): List<File> = resFolder.listFiles()!!.flatMap {
        if (it.isDirectory) {
            findResXmlFilesInFolder(it)
        } else if (it.isFile && it.name.endsWith(".xml")) {
            listOf(it)
        } else {
            emptyList()
        }
    }
}