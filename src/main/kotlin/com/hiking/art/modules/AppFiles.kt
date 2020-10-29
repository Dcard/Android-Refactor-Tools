package com.hiking.art.modules

import com.hiking.art.modules.configs.ConfigsHelper
import java.io.File

object AppFiles {

    val refactorRulesFile = File("refactorRules.json")

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