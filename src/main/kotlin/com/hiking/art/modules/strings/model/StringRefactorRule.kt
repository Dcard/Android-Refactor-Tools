package com.hiking.art.modules.strings.model

import kotlinx.serialization.Serializable

@Serializable
data class StringRefactorRule(
    val toName: String,
    val values: List<String>
)