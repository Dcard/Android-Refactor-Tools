package com.hiking.art.modules.configs

import kotlinx.serialization.json.Json
import java.io.File

object ConfigsHelper {
    fun readConfigs() = Json.decodeFromString(Configs.serializer(), File("configs.json").readText())
}