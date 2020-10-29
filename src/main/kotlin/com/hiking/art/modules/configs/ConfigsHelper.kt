package com.hiking.art.modules.configs

import com.hiking.art.modules.printlnErr
import kotlinx.serialization.json.Json
import java.io.File

object ConfigsHelper {

    private var configs: Configs? = null

    fun requestConfigs(): Configs? = configs ?: try {
        Json.decodeFromString(Configs.serializer(), File("configs.json").readText()).also {
            this@ConfigsHelper.configs = it
        }
    } catch (e: Exception) {
        printlnErr("Failed to read configs: ${e.javaClass.simpleName} ${e.message}")
        null
    }
}