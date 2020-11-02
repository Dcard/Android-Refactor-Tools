package com.hiking.art.modules.files

import com.hiking.art.modules.configs.ConfigsHelper
import com.hiking.art.modules.printlnErr
import java.io.File

object AppFiles {

    private val appFilesRoot = File("appFiles").apply { mkdirs() }
    val stringRenamingRulesFile = File(appFilesRoot, "stringRenamingRules.json")

    private var projectRoot: File? = null

    fun requestProjectRoot(): File? = projectRoot ?: run {
        val configs = ConfigsHelper.requestConfigs() ?: return null
        val root = File(configs.projectRoot)
        when {
            !root.exists() -> {
                printlnErr("Project root ${configs.projectRoot} does not exist!")
                null
            }
            !root.isDirectory -> {
                printlnErr("Project root ${configs.projectRoot} is not a directory!")
                null
            }
            else -> root
        }
    }
}