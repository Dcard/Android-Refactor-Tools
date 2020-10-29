package com.hiking.art.modules.refactor

import kotlinx.serialization.Serializable

@Serializable
data class StringRefactorRule(
    val fromName: String,
    val toName: String,
    val description: String? = null
)